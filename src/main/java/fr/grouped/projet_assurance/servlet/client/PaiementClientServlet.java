package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Paiements du client connecté : échéances à régler + historique. */
@WebServlet("/client/paiements")
public class PaiementClientServlet extends HttpServlet {

    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Le filtre garantit qu'un client est connecté (plus de client "3" par défaut)
        Utilisateur utilisateur = WebUtil.utilisateurConnecte(req);
        List<Paiement> paiements = paiementDAO.findByClientId(utilisateur.getIdUtilisateur());

        // Échéances à régler, la plus proche d'abord
        List<Paiement> aRegler = paiements.stream()
                .filter(p -> "EN_ATTENTE".equalsIgnoreCase(p.getStatut()))
                .sorted(Comparator.comparing(Paiement::getDatePaiement))
                .toList();
        // Historique : tout le reste, le plus récent d'abord (déjà trié par la requête)
        List<Paiement> historique = paiements.stream()
                .filter(p -> !"EN_ATTENTE".equalsIgnoreCase(p.getStatut()))
                .toList();

        BigDecimal totalPaye = historique.stream()
                .filter(p -> "EFFECTUE".equalsIgnoreCase(p.getStatut()))
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEnAttente = aRegler.stream()
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("paiements", paiements);
        req.setAttribute("aRegler", aRegler);
        req.setAttribute("historique", historique);
        req.setAttribute("totalPaye", totalPaye);
        req.setAttribute("totalEnAttente", totalEnAttente);
        req.setAttribute("prochaine", aRegler.isEmpty() ? null : aRegler.get(0));

        req.getRequestDispatcher("/WEB-INF/views/client/paiements.jsp").forward(req, resp);
    }
}
