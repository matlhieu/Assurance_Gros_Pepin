package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Sinistre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agent/sinistres")
public class ListerSinistresServlet extends HttpServlet {

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        List<Sinistre> sinistres = new ArrayList<>();
        sinistres.addAll(sinistreDAO.findByStatut("DECLARE"));
        sinistres.addAll(sinistreDAO.findByStatut("EN_ANALYSE"));
        sinistres.addAll(sinistreDAO.findByStatut("ACCEPTE"));

        req.setAttribute("sinistres", sinistres);
        req.getRequestDispatcher("/WEB-INF/views/agent/liste-sinistres.jsp").forward(req, resp);
    }
}