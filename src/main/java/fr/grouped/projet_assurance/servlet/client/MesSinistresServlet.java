package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.model.Sinistre;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/client/sinistres")
public class MesSinistresServlet extends HttpServlet {
    
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute("utilisateur");
        List<Sinistre> sinistres = sinistreDAO.findByClient(u.getIdUtilisateur());
        
        req.setAttribute("sinistres", sinistres);
        req.getRequestDispatcher("/WEB-INF/views/client/mes-sinistres.jsp").forward(req, resp);
    }
}