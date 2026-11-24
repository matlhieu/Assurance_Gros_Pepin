package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/agent/paiements")
public class PaiementAgentServlet extends HttpServlet {

    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paiement> paiements = paiementDAO.findAll();
        List<Contrat> contrats = contratDAO.findAll();

        req.setAttribute("paiements", paiements);
        req.setAttribute("contrats", contrats);

        req.getRequestDispatcher("/WEB-INF/paiements-agent.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("updateStatut".equalsIgnoreCase(action) || "encaisser".equalsIgnoreCase(action)) {
                int idPaiement = Integer.parseInt(req.getParameter("idPaiement"));
                Paiement p = paiementDAO.findById(idPaiement);
                if (p != null) {
                    String newStatut = req.getParameter("statut");
                    if (newStatut == null || newStatut.isBlank()) {
                        newStatut = "EFFECTUE";
                    }
                    p.setStatut(newStatut);
                    p.setDatePaiement(LocalDate.now());

                    String mode = req.getParameter("modePaiement");
                    if (mode != null && !mode.isBlank()) {
                        p.setModePaiement(mode);
                    }
                    paiementDAO.update(p);
                }
            } else {
                int idContrat = Integer.parseInt(req.getParameter("idContrat"));
                BigDecimal montant = new BigDecimal(req.getParameter("montant"));
                String modePaiement = req.getParameter("modePaiement");
                String statut = req.getParameter("statut");
                if (statut == null || statut.isBlank()) {
                    statut = "EFFECTUE";
                }
                String dateStr = req.getParameter("datePaiement");
                LocalDate datePaiement = (dateStr != null && !dateStr.isBlank()) 
                        ? LocalDate.parse(dateStr) 
                        : LocalDate.now();

                Contrat contrat = contratDAO.findById(idContrat);
                if (contrat != null) {
                    Paiement nouveauPaiement = new Paiement();
                    nouveauPaiement.setMontant(montant);
                    nouveauPaiement.setDatePaiement(datePaiement);
                    nouveauPaiement.setModePaiement(modePaiement);
                    nouveauPaiement.setStatut(statut);
                    nouveauPaiement.setContrat(contrat);

                    paiementDAO.save(nouveauPaiement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/agent/paiements");
    }
}
