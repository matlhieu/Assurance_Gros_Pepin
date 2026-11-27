package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Garantie;
import fr.grouped.projet_assurance.model.TypeAssurance;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Gestion des contrats, partagée entre l'agent (/agent/contrats) et l'admin (/admin/contrats).
 *  GET  ?action=list[&statut=..&q=..]   liste filtrable
 *  GET  ?action=form[&id=..]            création / modification
 *  GET  ?action=detail&id=..            fiche contrat
 *  GET  ?action=delete&id=..            suppression (admin uniquement)
 *  POST action=save                     enregistrement
 * Les JSP construisent leurs liens avec ${baseUrl} pour rester dans l'espace courant.
 */
@WebServlet({"/admin/contrats", "/agent/contrats"})
public class ContratServlet extends HttpServlet {

    private ContratDAO contratDAO;
    private TypeAssuranceDAO typeAssuranceDAO;
    private GarantieDAO garantieDAO;
    private UtilisateurDAO utilisateurDAO;
    private final fr.grouped.projet_assurance.service.PaiementService paiementService =
            new fr.grouped.projet_assurance.service.PaiementService();

    @Override
    public void init() throws ServletException {
        contratDAO = new ContratDAOImpl();
        typeAssuranceDAO = new TypeAssuranceDAOImpl();
        garantieDAO = new GarantieDAOImpl();
        utilisateurDAO = new UtilisateurDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebUtil.consommerFlash(request);
        String base = request.getContextPath() + request.getServletPath();
        request.setAttribute("baseUrl", base);
        request.setAttribute("estAdmin", "ADMIN".equals(WebUtil.utilisateurConnecte(request).getRole()));

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "form" -> {
                Integer id = WebUtil.parseId(request.getParameter("id"));
                if (id != null) {
                    Contrat contrat = contratDAO.findByIdAvecDetails(id);
                    if (contrat == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                    request.setAttribute("contrat", contrat);
                }
                afficherFormulaire(request, response);
            }
            case "detail" -> {
                Integer id = WebUtil.parseId(request.getParameter("id"));
                Contrat contrat = (id == null) ? null : contratDAO.findByIdAvecDetails(id);
                if (contrat == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                request.setAttribute("contrat", contrat);
                request.getRequestDispatcher("/WEB-INF/views/gestion/contrats/detail.jsp").forward(request, response);
            }
            case "delete" -> {
                // La suppression est réservée à l'administrateur
                if (!"ADMIN".equals(WebUtil.utilisateurConnecte(request).getRole())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                Integer id = WebUtil.parseId(request.getParameter("id"));
                try {
                    if (id != null) contratDAO.delete(id);
                    WebUtil.flash(request, "succes", "Contrat supprimé.");
                } catch (RuntimeException e) {
                    // Contrat référencé par des sinistres ou des paiements (clé étrangère)
                    WebUtil.flash(request, "erreur", "Ce contrat a des sinistres ou des paiements : résiliez-le plutôt que de le supprimer.");
                }
                response.sendRedirect(base);
            }
            default -> afficherListe(request, response);
        }
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Contrat> contrats = contratDAO.findAllAvecDetails();

        Map<String, Long> compteurs = new LinkedHashMap<>();
        for (StatutContrat st : StatutContrat.values()) {
            compteurs.put(st.name(), contrats.stream().filter(c -> c.getStatut() == st).count());
        }
        request.setAttribute("compteurs", compteurs);
        request.setAttribute("nbTotal", contrats.size());
        request.setAttribute("statuts", StatutContrat.values());

        String filtre = request.getParameter("statut");
        if (filtre != null && compteurs.containsKey(filtre)) {
            StatutContrat st = StatutContrat.valueOf(filtre);
            contrats = contrats.stream().filter(c -> c.getStatut() == st).toList();
            request.setAttribute("filtreStatut", filtre);
        }
        String q = request.getParameter("q");
        if (q != null && !q.isBlank()) {
            String r = q.trim().toLowerCase(Locale.ROOT);
            contrats = contrats.stream().filter(c ->
                    c.getNumContrat().toLowerCase(Locale.ROOT).contains(r)
                 || c.getUtilisateur().getNom().toLowerCase(Locale.ROOT).contains(r)
                 || c.getUtilisateur().getPrenom().toLowerCase(Locale.ROOT).contains(r)
                 || c.getTypeAssurance().getNom().toLowerCase(Locale.ROOT).contains(r))
                .toList();
        }
        request.setAttribute("contrats", contrats);
        request.getRequestDispatcher("/WEB-INF/views/gestion/contrats/liste.jsp").forward(request, response);
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Types avec leurs garanties : le formulaire pré-remplit la prime et coche les garanties du produit choisi
        request.setAttribute("typesAssurance", typeAssuranceDAO.findAllAvecGaranties());
        if (request.getAttribute("contrat") == null) {
            // Valeurs proposées pour un nouveau contrat (modifiables)
            request.setAttribute("numeroPropose", "CTR-" + LocalDate.now().getYear() + "-"
                    + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT));
            request.setAttribute("debutPropose", LocalDate.now());
            request.setAttribute("finProposee", LocalDate.now().plusYears(1).minusDays(1));
        }
        request.setAttribute("toutesLesGaranties", garantieDAO.findAll());
        request.setAttribute("clients", utilisateurDAO.findByRole("CLIENT"));
        request.setAttribute("statuts", StatutContrat.values());
        request.getRequestDispatcher("/WEB-INF/views/gestion/contrats/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String base = request.getContextPath() + request.getServletPath();
        request.setAttribute("baseUrl", base);
        request.setAttribute("estAdmin", "ADMIN".equals(WebUtil.utilisateurConnecte(request).getRole()));

        if (!"save".equals(request.getParameter("action"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, String> erreurs = new LinkedHashMap<>();
        Integer id = WebUtil.parseId(request.getParameter("id"));
        String numContrat = request.getParameter("numContrat") == null ? "" : request.getParameter("numContrat").trim();
        Integer idClient = WebUtil.parseId(request.getParameter("idUtilisateur"));
        Integer idType = WebUtil.parseId(request.getParameter("idTypeAssurance"));
        BigDecimal prime = WebUtil.parseMontant(request.getParameter("prime"));
        LocalDate debut = parseDate(request.getParameter("dateDebut"));
        LocalDate fin = parseDate(request.getParameter("dateFin"));
        StatutContrat statut;
        try {
            statut = StatutContrat.valueOf(request.getParameter("statut"));
        } catch (RuntimeException e) {
            statut = StatutContrat.EN_ATTENTE;
        }

        if (numContrat.isEmpty() || numContrat.length() > 50) erreurs.put("numContrat", "Numéro de contrat obligatoire (50 caractères maximum).");
        Utilisateur client = (idClient == null) ? null : utilisateurDAO.findById(idClient);
        if (client == null || !"CLIENT".equals(client.getRole())) erreurs.put("idUtilisateur", "Choisissez un client.");
        TypeAssurance type = (idType == null) ? null : typeAssuranceDAO.findById(idType);
        if (type == null) erreurs.put("idTypeAssurance", "Choisissez un type d'assurance.");
        if (prime == null) erreurs.put("prime", "Prime invalide.");
        if (debut == null) erreurs.put("dateDebut", "Date d'effet invalide.");
        if (fin == null) erreurs.put("dateFin", "Date d'échéance invalide.");
        else if (debut != null && !fin.isAfter(debut)) erreurs.put("dateFin", "L'échéance doit être après la date d'effet.");

        List<Garantie> garanties = new ArrayList<>();
        String[] ids = request.getParameterValues("garanties");
        if (ids != null) {
            for (String g : ids) {
                Integer gid = WebUtil.parseId(g);
                Garantie garantie = (gid == null) ? null : garantieDAO.findById(gid);
                if (garantie != null) garanties.add(garantie);
            }
        }

        if (!erreurs.isEmpty()) {
            request.setAttribute("erreurs", erreurs);
            if (id != null) request.setAttribute("contrat", contratDAO.findByIdAvecDetails(id));
            afficherFormulaire(request, response);
            return;
        }

        Contrat contrat = new Contrat();
        contrat.setIdContrat(id);
        contrat.setNumContrat(numContrat);
        contrat.setUtilisateur(client);
        contrat.setTypeAssurance(type);
        contrat.setPrime(prime);
        contrat.setDateDebut(debut);
        contrat.setDateFin(fin);
        contrat.setStatut(statut);
        contrat.setGaranties(garanties);

        try {
            if (id == null) {
                contratDAO.save(contrat);
                String message = "Contrat " + numContrat + " créé.";
                if (statut == StatutContrat.ACTIF) {
                    int n = paiementService.genererEcheancier(contrat, nbEcheances(request),
                            debut.isAfter(LocalDate.now()) ? debut : LocalDate.now());
                    if (n > 0) message += " " + n + " échéance(s) de paiement générée(s).";
                }
                WebUtil.flash(request, "succes", message);
            } else {
                contratDAO.update(contrat);
                WebUtil.flash(request, "succes", "Contrat " + numContrat + " mis à jour.");
            }
        } catch (RuntimeException e) {
            // le plus souvent : numéro de contrat déjà utilisé (colonne UNIQUE)
            erreurs.put("numContrat", "Ce numéro de contrat existe déjà.");
            request.setAttribute("erreurs", erreurs);
            if (id != null) request.setAttribute("contrat", contratDAO.findByIdAvecDetails(id));
            afficherFormulaire(request, response);
            return;
        }
        response.sendRedirect(base + "?action=detail&id=" + contrat.getIdContrat());
    }

    /** Nombre d'échéances choisi dans le formulaire : 12 (mensuel), 4 (trimestriel) ou 1 (annuel). */
    static int nbEcheances(HttpServletRequest request) {
        String v = request.getParameter("echeances");
        return "1".equals(v) ? 1 : "4".equals(v) ? 4 : 12;
    }

    private static LocalDate parseDate(String s) {
        try {
            return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
