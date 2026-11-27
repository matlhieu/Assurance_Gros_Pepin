package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.service.PaiementService;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Gestion des paiements par un agent :
 *  GET  /agent/paiements[?statut=..&q=..]       -> liste filtrable + formulaire d'enregistrement
 *  POST action=encaisser (idPaiement, modePaiement) -> une échéance en attente passe à EFFECTUE
 *  POST action=enregistrer (idContrat, montant, ...) -> nouveau paiement
 */
@WebServlet("/agent/paiements")
public class PaiementAgentServlet extends HttpServlet {

    public static final List<String> STATUTS = List.of("EN_ATTENTE", "EFFECTUE", "ECHOUE");
    public static final List<String> MODES = List.of("CARTE", "VIREMENT", "PRELEVEMENT");

    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.consommerFlash(req);

        List<Paiement> paiements = paiementDAO.findAll();

        Map<String, Long> compteurs = new LinkedHashMap<>();
        for (String st : STATUTS) {
            compteurs.put(st, paiements.stream().filter(p -> st.equals(p.getStatut())).count());
        }
        BigDecimal totalEncaisse = somme(paiements, "EFFECTUE");
        BigDecimal totalEnAttente = somme(paiements, "EN_ATTENTE");
        long enRetard = paiements.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut()) && p.getDatePaiement().isBefore(LocalDate.now()))
                .count();

        String filtre = req.getParameter("statut");
        if (filtre != null && STATUTS.contains(filtre)) {
            paiements = paiements.stream().filter(p -> filtre.equals(p.getStatut())).toList();
            req.setAttribute("filtreStatut", filtre);
        }
        String q = req.getParameter("q");
        if (q != null && !q.isBlank()) {
            String r = q.trim().toLowerCase(Locale.ROOT);
            paiements = paiements.stream().filter(p ->
                    p.getContrat().getNumContrat().toLowerCase(Locale.ROOT).contains(r)
                 || p.getContrat().getUtilisateur().getNom().toLowerCase(Locale.ROOT).contains(r)
                 || p.getContrat().getUtilisateur().getPrenom().toLowerCase(Locale.ROOT).contains(r))
                .toList();
        }

        req.setAttribute("paiements", paiements);
        req.setAttribute("compteurs", compteurs);
        req.setAttribute("nbTotal", compteurs.values().stream().mapToLong(Long::longValue).sum());
        req.setAttribute("totalEncaisse", totalEncaisse);
        req.setAttribute("totalEnAttente", totalEnAttente);
        req.setAttribute("enRetard", enRetard);
        req.setAttribute("aujourdhui", LocalDate.now());
        req.setAttribute("statuts", STATUTS);
        req.setAttribute("modes", MODES);
        // Contrats avec client et type chargés : le formulaire affiche c.utilisateur.nom
        req.setAttribute("contrats", contratDAO.findAllAvecDetails());

        req.getRequestDispatcher("/WEB-INF/views/agent/paiements.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("encaisser".equals(action)) {
                Integer id = WebUtil.parseId(req.getParameter("idPaiement"));
                String mode = req.getParameter("modePaiement");
                if (id == null) throw new IllegalArgumentException("Paiement introuvable.");
                if (mode == null || !MODES.contains(mode)) {
                    Paiement p = paiementDAO.findById(id);
                    mode = (p != null) ? p.getModePaiement() : "VIREMENT";
                }
                paiementService.encaisserPaiement(id, mode);
                WebUtil.flash(req, "succes", "Paiement encaissé.");

            } else if ("enregistrer".equals(action)) {
                Integer idContrat = WebUtil.parseId(req.getParameter("idContrat"));
                BigDecimal montant = WebUtil.parseMontant(req.getParameter("montant"));
                String mode = req.getParameter("modePaiement");
                String statut = req.getParameter("statut");
                Contrat contrat = (idContrat == null) ? null : contratDAO.findById(idContrat);

                if (contrat == null) throw new IllegalArgumentException("Choisissez un contrat.");
                if (montant == null) throw new IllegalArgumentException("Montant invalide.");
                if (mode == null || !MODES.contains(mode)) throw new IllegalArgumentException("Mode de règlement invalide.");
                if (statut == null || !Set.of("EFFECTUE", "EN_ATTENTE").contains(statut)) statut = "EFFECTUE";

                LocalDate date = LocalDate.now();
                String dateStr = req.getParameter("datePaiement");
                if (dateStr != null && !dateStr.isBlank()) {
                    try {
                        date = LocalDate.parse(dateStr);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Date invalide.");
                    }
                }

                Paiement p = new Paiement();
                p.setContrat(contrat);
                p.setMontant(montant);
                p.setModePaiement(mode);
                p.setStatut(statut);
                p.setDatePaiement(date);
                paiementDAO.save(p);
                WebUtil.flash(req, "succes", "Paiement de " + WebUtil.euros(montant) + " enregistré sur le contrat "
                        + contrat.getNumContrat() + ".");
            } else {
                throw new IllegalArgumentException("Action inconnue.");
            }
        } catch (Exception e) {
            WebUtil.flash(req, "erreur", e.getMessage() != null ? e.getMessage() : "Opération impossible.");
        }
        resp.sendRedirect(req.getContextPath() + "/agent/paiements");
    }

    private static BigDecimal somme(List<Paiement> paiements, String statut) {
        return paiements.stream().filter(p -> statut.equals(p.getStatut()))
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
