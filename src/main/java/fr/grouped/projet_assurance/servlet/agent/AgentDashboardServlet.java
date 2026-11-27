package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/agent/dashboard")
public class AgentDashboardServlet extends HttpServlet {

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Sinistre> sinistres = sinistreDAO.findAllAvecDetails();
        List<Paiement> paiementsEnAttente = paiementDAO.findEnAttente();

        req.setAttribute("nbDeclares", compter(sinistres, "DECLARE"));
        req.setAttribute("nbEnAnalyse", compter(sinistres, "EN_ANALYSE"));
        req.setAttribute("nbAcceptes", compter(sinistres, "ACCEPTE"));
        req.setAttribute("nbRefuses", compter(sinistres, "REFUSE"));
        req.setAttribute("nbIndemnises", compter(sinistres, "INDEMNISE"));
        req.setAttribute("nbSinistres", sinistres.size());
        req.setAttribute("nbPaiementsEnAttente", paiementsEnAttente.size());
        req.setAttribute("montantPaiementsEnAttente", paiementsEnAttente.stream()
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add));
        req.setAttribute("nbContratsEnAttente", contratDAO.findByStatut(StatutContrat.EN_ATTENTE).size());

        // Sinistres qui demandent une action de l'agent, les plus anciens d'abord
        List<Sinistre> aTraiter = sinistres.stream()
                .filter(s -> "DECLARE".equals(s.getStatut()) || "EN_ANALYSE".equals(s.getStatut())
                          || "ACCEPTE".equals(s.getStatut()))
                .sorted((a, b) -> a.getDateDeclaration().compareTo(b.getDateDeclaration()))
                .limit(8)
                .toList();
        req.setAttribute("sinistresATraiter", aTraiter);

        req.getRequestDispatcher("/WEB-INF/views/agent/dashboard.jsp").forward(req, resp);
    }

    private static long compter(List<Sinistre> sinistres, String statut) {
        return sinistres.stream().filter(s -> statut.equals(s.getStatut())).count();
    }
}
