package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
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
import java.util.List;

@WebServlet("/client/dashboard")
public class ClientDashboardServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        int clientId = u.getIdUtilisateur();

        List<Contrat> contrats = contratDAO.findByClientAvecDetails(clientId);
        List<Sinistre> sinistres = sinistreDAO.findByClientAvecDetails(clientId);
        List<Paiement> paiements = paiementDAO.findByClientId(clientId);

        long contratsActifs = contrats.stream().filter(c -> c.getStatut() == StatutContrat.ACTIF).count();
        long sinistresEnCours = sinistres.stream()
                .filter(s -> !"REFUSE".equals(s.getStatut()) && !"INDEMNISE".equals(s.getStatut()))
                .count();
        BigDecimal montantEnAttente = paiements.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut()))
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Échéance en attente la plus proche (pour la carte "Prochaine échéance")
        paiements.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut()) && p.getDatePaiement() != null)
                .min((a, b) -> a.getDatePaiement().compareTo(b.getDatePaiement()))
                .ifPresent(p -> {
                    req.setAttribute("prochaineEcheanceDate", p.getDatePaiement());
                    req.setAttribute("prochaineEcheanceMontant", p.getMontant());
                });

        req.setAttribute("contratsActifs", contratsActifs);
        req.setAttribute("sinistresEnCours", sinistresEnCours);
        req.setAttribute("montantEnAttente", montantEnAttente);
        req.setAttribute("nbContrats", contrats.size());
        req.setAttribute("nbSinistres", sinistres.size());

        req.getRequestDispatcher("/WEB-INF/views/client/dashboard.jsp").forward(req, resp);
    }
}
