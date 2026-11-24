package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/client/paiements")
public class PaiementClientServlet extends HttpServlet {

    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        int clientId = (utilisateur != null) ? utilisateur.getIdUtilisateur() : 3;

        List<Paiement> paiements = paiementDAO.findByClientId(clientId);

        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalEnAttente = BigDecimal.ZERO;
        for (Paiement p : paiements) {
            if ("EFFECTUE".equalsIgnoreCase(p.getStatut())) {
                totalPaye = totalPaye.add(p.getMontant());
            } else if ("EN_ATTENTE".equalsIgnoreCase(p.getStatut())) {
                totalEnAttente = totalEnAttente.add(p.getMontant());
            }
        }

        req.setAttribute("paiements", paiements);
        req.setAttribute("totalPaye", totalPaye);
        req.setAttribute("totalEnAttente", totalEnAttente);

        req.getRequestDispatcher("/WEB-INF/paiements-client.jsp").forward(req, resp);
    }
}
