package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Sinistre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/sinistres/detail")
public class DetailSinistreServlet extends HttpServlet {

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                Integer id = Integer.parseInt(idStr);
                Sinistre s = sinistreDAO.findById(id);
                if (s != null) {
                    req.setAttribute("sinistre", s);
                    req.getRequestDispatcher("/WEB-INF/views/agent/detail-sinistre.jsp").forward(req, resp);
                    return;
                }
            } catch (NumberFormatException e) {
                // Ignorer
            }
        }
        resp.sendRedirect(req.getContextPath() + "/agent/sinistres?error=SinistreIntrouvable");
    }
}