package fr.grouped.projet_assurance.servlet;

import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.model.TypeAssurance;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/types") 
public class TypeAssuranceServlet extends HttpServlet {

    private TypeAssuranceDAO typeDAO;

    @Override
    public void init() throws ServletException {
        // On initialise le DAO une seule fois quand le servlet démarre
        typeDAO = new TypeAssuranceDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                List<TypeAssurance> types = typeDAO.findAll();
                request.setAttribute("types", types);
                request.getRequestDispatcher("/WEB-INF/views/types/liste.jsp").forward(request, response);
                break;
                
            case "form":
                
                String idParam = request.getParameter("id");
                if (idParam != null) {
                    TypeAssurance type = typeDAO.findById(Long.parseLong(idParam));
                    request.setAttribute("type", type);
                }
                request.getRequestDispatcher("/WEB-INF/views/types/form.jsp").forward(request, response);
                break;
                
            case "delete":
                Long id = Long.parseLong(request.getParameter("id"));
                typeDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/types?action=list");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            // 1. Récupération des données du formulaire
            String idParam = request.getParameter("id");
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String prixStr = request.getParameter("prixBase");

            // 2. Création de l'objet
            TypeAssurance type = new TypeAssurance();
            if (idParam != null && !idParam.isEmpty()) {
                type.setIdTypeAssurance(Long.parseLong(idParam));
            }
            type.setNom(nom);
            type.setDescription(description);
            type.setPrixBase(new BigDecimal(prixStr));

            if (type.getIdTypeAssurance() == null) {
                typeDAO.save(type);
            } else {
                typeDAO.update(type);
            }

            response.sendRedirect(request.getContextPath() + "/types?action=list");
        }
    }
}