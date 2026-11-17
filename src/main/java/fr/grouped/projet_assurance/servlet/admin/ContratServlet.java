package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.TypeAssurance;
import fr.grouped.projet_assurance.model.enums.StatutContrat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.model.Garantie;
import java.util.ArrayList;



@WebServlet("/admin/contrats")
public class ContratServlet extends HttpServlet {

    private ContratDAO contratDAO;
    private TypeAssuranceDAO typeAssuranceDAO;

    @Override
    public void init() throws ServletException {
        contratDAO = new ContratDAOImpl();
        typeAssuranceDAO = new TypeAssuranceDAOImpl();
        garantieDAO = new GarantieDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                List<Contrat> contrats = contratDAO.findAll();
                request.setAttribute("contrats", contrats);
                request.getRequestDispatcher("/WEB-INF/views/admin/contrats/liste.jsp").forward(request, response);
                break;
                
            case "form":
                // On charge les types d'assurance pour le menu déroulant du formulaire
                request.setAttribute("typesAssurance", typeAssuranceDAO.findAll());
                
                // On charge TOUTES les garanties pour les cases à cocher
                request.setAttribute("toutesLesGaranties", garantieDAO.findAll()); 
                
                String idParam = request.getParameter("id");
                if (idParam != null) {
                    Contrat contrat = contratDAO.findById(Long.parseLong(idParam));
                    request.setAttribute("contrat", contrat);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/contrats/form.jsp").forward(request, response);
                break;
                
            case "delete":
                Long id = Long.parseLong(request.getParameter("id"));
                contratDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/contrats?action=list");
                break;
                
            case "detail":
                Long idDetail = Long.parseLong(request.getParameter("id"));
                Contrat contratDetail = contratDAO.findById(idDetail);
                request.setAttribute("contrat", contratDetail);
                request.getRequestDispatcher("/WEB-INF/views/admin/contrats/detail.jsp").forward(request, response);
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
            String numContrat = request.getParameter("numContrat");
            String dateDebutStr = request.getParameter("dateDebut");
            String dateFinStr = request.getParameter("dateFin");
            String primeStr = request.getParameter("prime");
            String statutStr = request.getParameter("statut");
            String idTypeStr = request.getParameter("idTypeAssurance");

            Contrat contrat = new Contrat();
            if (idParam != null && !idParam.isEmpty()) {
                contrat.setIdContrat(Long.parseLong(idParam));
            }
            
            contrat.setNumContrat(numContrat);
            contrat.setDateDebut(LocalDate.parse(dateDebutStr));
            contrat.setDateFin(LocalDate.parse(dateFinStr));
            contrat.setPrime(new BigDecimal(primeStr));
            contrat.setStatut(StatutContrat.valueOf(statutStr));
            
            // On associe le type d'assurance
            TypeAssurance type = typeAssuranceDAO.findById(Long.parseLong(idTypeStr));
            contrat.setTypeAssurance(type);
            
            // Récupérer les garanties cochées dans le formulaire
            String[] garantiesIds = request.getParameterValues("garanties");
            List<Garantie> garantiesChoisies = new ArrayList<>();

            if (garantiesIds != null) {
                for (String gId : garantiesIds) {
                    Garantie g = garantieDAO.findById(Long.parseLong(gId));
                    if (g != null) {
                        garantiesChoisies.add(g);
                    }
                }
            }
            contrat.setGaranties(garantiesChoisies);

            // Enregistrer ou mettre à jour
            if (contrat.getIdContrat() == null) {
                contratDAO.save(contrat);
            } else {
                contratDAO.update(contrat);
            }

            response.sendRedirect(request.getContextPath() + "/admin/contrats?action=list");
        }
    }
}