package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.model.Garantie;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/garanties")
public class GarantieServlet extends HttpServlet {

    private GarantieDAO garantieDAO;

    @Override
    public void init() throws ServletException {
        garantieDAO = new GarantieDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                List<Garantie> garanties = garantieDAO.findAll();
                request.setAttribute("garanties", garanties);
                request.getRequestDispatcher("/WEB-INF/views/admin/garanties/liste.jsp").forward(request, response);
                break;
                
            case "form":
                String idParam = request.getParameter("id");
                if (idParam != null) {
                    Garantie garantie = garantieDAO.findById(Integer.parseInt(idParam));
                    request.setAttribute("garantie", garantie);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/garanties/form.jsp").forward(request, response);
                break;
                
            case "delete":
                Integer id = Integer.parseInt(request.getParameter("id"));
                garantieDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/garanties?action=list");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            String idParam = request.getParameter("id");
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");

            Garantie garantie = new Garantie();
            if (idParam != null && !idParam.isEmpty()) {
                garantie.setIdGarantie(Integer.parseInt(idParam));
            }
            garantie.setNom(nom);
            garantie.setDescription(description);

            if (garantie.getIdGarantie() == null) {
                garantieDAO.save(garantie);
            } else {
                garantieDAO.update(garantie);
            }

            response.sendRedirect(request.getContextPath() + "/admin/garanties?action=list");
        }
    }
}