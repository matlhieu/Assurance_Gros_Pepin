package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Sinistre;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Portefeuille clients de l'agent :
 *  GET /agent/clients[?q=..]  -> liste avec indicateurs par client
 *  GET /agent/clients?id=..   -> fiche client (contrats, sinistres, paiements)
 */
@WebServlet("/agent/clients")
public class ClientAgentServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    /** Ligne du tableau des clients. */
    public record LigneClient(Utilisateur client, long nbContrats, long nbActifs, long nbSinistresEnCours) {
        public Utilisateur getClient() { return client; }
        public long getNbContrats() { return nbContrats; }
        public long getNbActifs() { return nbActifs; }
        public long getNbSinistresEnCours() { return nbSinistresEnCours; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = WebUtil.parseId(req.getParameter("id"));
        if (id != null) {
            afficherFiche(id, req, resp);
            return;
        }

        List<Contrat> contrats = contratDAO.findAllAvecDetails();
        List<Sinistre> sinistres = sinistreDAO.findAllAvecDetails();

        // Regroupement par id client (une seule requête par table au lieu d'une par client)
        Map<Integer, long[]> stats = new HashMap<>();   // [contrats, actifs, sinistres en cours]
        for (Contrat c : contrats) {
            long[] s = stats.computeIfAbsent(c.getUtilisateur().getIdUtilisateur(), k -> new long[3]);
            s[0]++;
            if (c.getStatut() == StatutContrat.ACTIF) s[1]++;
        }
        for (Sinistre s : sinistres) {
            if (!"REFUSE".equals(s.getStatut()) && !"INDEMNISE".equals(s.getStatut())) {
                stats.computeIfAbsent(s.getContrat().getUtilisateur().getIdUtilisateur(), k -> new long[3])[2]++;
            }
        }

        List<Utilisateur> clients = utilisateurDAO.findByRole("CLIENT");
        String q = req.getParameter("q");
        if (q != null && !q.isBlank()) {
            String r = q.trim().toLowerCase(Locale.ROOT);
            clients = clients.stream().filter(u ->
                    u.getNom().toLowerCase(Locale.ROOT).contains(r)
                 || u.getPrenom().toLowerCase(Locale.ROOT).contains(r)
                 || u.getEmail().toLowerCase(Locale.ROOT).contains(r))
                .toList();
        }

        List<LigneClient> lignes = clients.stream()
                .sorted(Comparator.comparing(Utilisateur::getNom, String.CASE_INSENSITIVE_ORDER))
                .map(u -> {
                    long[] s = stats.getOrDefault(u.getIdUtilisateur(), new long[3]);
                    return new LigneClient(u, s[0], s[1], s[2]);
                })
                .toList();

        req.setAttribute("lignes", lignes);
        req.setAttribute("nbClients", utilisateurDAO.findByRole("CLIENT").size());
        req.getRequestDispatcher("/WEB-INF/views/agent/clients/liste.jsp").forward(req, resp);
    }

    private void afficherFiche(Integer id, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utilisateur client = utilisateurDAO.findById(id);
        if (client == null || !"CLIENT".equals(client.getRole())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        List<Paiement> paiements = paiementDAO.findByClientId(id);
        BigDecimal enAttente = paiements.stream().filter(p -> "EN_ATTENTE".equals(p.getStatut()))
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paye = paiements.stream().filter(p -> "EFFECTUE".equals(p.getStatut()))
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("client", client);
        req.setAttribute("contrats", contratDAO.findByClientAvecDetails(id));
        req.setAttribute("sinistres", sinistreDAO.findByClientAvecDetails(id));
        req.setAttribute("paiements", paiements);
        req.setAttribute("totalEnAttente", enAttente);
        req.setAttribute("totalPaye", paye);
        req.getRequestDispatcher("/WEB-INF/views/agent/clients/fiche.jsp").forward(req, resp);
    }
}
