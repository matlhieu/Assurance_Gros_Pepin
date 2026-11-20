package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.enums.StatutContrat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/contrats/action")
public class ContratActionServlet extends HttpServlet {

    private ContratDAO contratDAO;

    @Override
    public void init() throws ServletException {
        contratDAO = new ContratDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if (idParam != null && action != null) {
            Integer id = Integer.parseInt(idParam);
            Contrat contrat = contratDAO.findById(id);

            if (contrat != null) {
                switch (action) {
                    case "suspendre":
                        contrat.setStatut(StatutContrat.SUSPENDU);
                        break;
                    case "resilier":
                        contrat.setStatut(StatutContrat.RESILIE);
                        break;
                    case "renouveler":
                        contrat.setStatut(StatutContrat.ACTIF);
                        // On ajoute 1 an à la date de fin
                        contrat.setDateFin(contrat.getDateFin().plusYears(1));
                        break;
                }
                contratDAO.update(contrat);
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/contrats?action=list");
    }
}