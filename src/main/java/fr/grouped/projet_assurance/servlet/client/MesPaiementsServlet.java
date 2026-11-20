package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/client/paiements")
public class MesPaiementsServlet extends HttpServlet {
    
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = (Utilisateur) req.getSession().getAttribute("utilisateur");
        List<Contrat> contrats = contratDAO.findByClient(u.getIdUtilisateur());
        
        List<Paiement> tousLesPaiements = new ArrayList<>();
        for (Contrat c : contrats) {
            tousLesPaiements.addAll(paiementDAO.findByContrat(c.getIdContrat()));
        }
        
        req.setAttribute("paiements", tousLesPaiements);
        req.getRequestDispatcher("/WEB-INF/views/client/mes-paiements.jsp").forward(req, resp);
    }
}