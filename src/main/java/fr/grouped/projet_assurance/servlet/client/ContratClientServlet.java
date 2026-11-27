package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Contrats du client connecté : liste, ou détail avec ?id=... */
@WebServlet("/client/contrats")
public class ContratClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        Integer id = WebUtil.parseId(req.getParameter("id"));

        if (id == null) {
            req.setAttribute("contrats", contratDAO.findByClientAvecDetails(u.getIdUtilisateur()));
            req.getRequestDispatcher("/WEB-INF/views/client/contrats/liste.jsp").forward(req, resp);
            return;
        }

        Contrat contrat = contratDAO.findByIdAvecDetails(id);
        // Un client ne voit que SES contrats (sinon 404, pour ne pas révéler qu'il existe)
        if (contrat == null || contrat.getUtilisateur().getIdUtilisateur() != u.getIdUtilisateur()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("contrat", contrat);
        req.getRequestDispatcher("/WEB-INF/views/client/contrats/detail.jsp").forward(req, resp);
    }
}
