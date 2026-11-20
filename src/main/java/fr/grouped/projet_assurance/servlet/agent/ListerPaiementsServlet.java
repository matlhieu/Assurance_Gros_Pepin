package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Paiement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/agent/paiements")
public class ListerPaiementsServlet extends HttpServlet {

    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     
        List<Paiement> paiements = paiementDAO.findEnAttente();

        req.setAttribute("paiements", paiements);
        req.getRequestDispatcher("/WEB-INF/views/agent/liste-paiements.jsp").forward(req, resp);
    }
}