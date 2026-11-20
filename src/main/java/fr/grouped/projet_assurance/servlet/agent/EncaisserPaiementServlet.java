package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.service.PaiementService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/agent/paiements/encaisser")
public class EncaisserPaiementServlet extends HttpServlet {

    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String paiementIdStr = req.getParameter("paiementId");
        String modePaiement = req.getParameter("modePaiement");

        try {
            if (paiementIdStr == null || modePaiement == null) {
                throw new Exception("Paramètres manquants.");
            }
            
            Integer paiementId = Integer.parseInt(paiementIdStr);
            paiementService.encaisserPaiement(paiementId, modePaiement);
            
            resp.sendRedirect(req.getContextPath() + "/agent/paiements?success=true");
            
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/agent/paiements?error=" + e.getMessage());
        }
    }
}