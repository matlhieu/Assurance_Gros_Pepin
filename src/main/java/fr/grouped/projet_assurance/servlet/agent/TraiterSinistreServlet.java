package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.service.SinistreService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/agent/sinistres/traiter")
public class TraiterSinistreServlet extends HttpServlet {

    private final SinistreService sinistreService = new SinistreService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String sinistreIdStr = req.getParameter("sinistreId");
        String action = req.getParameter("action");

        if (sinistreIdStr == null || action == null) {
            resp.sendRedirect(req.getContextPath() + "/agent/sinistres?error=ParametresManquants");
            return;
        }

        try {
            Integer sinistreId = Integer.parseInt(sinistreIdStr);
            String commentaire = req.getParameter("commentaire");

            switch (action) {
                case "PRENDRE_EN_CHARGE":
                    sinistreService.changerStatut(sinistreId, "EN_ANALYSE", commentaire);
                    break;
                case "ACCEPTER":
                    sinistreService.changerStatut(sinistreId, "ACCEPTE", commentaire);
                    break;
                case "REFUSER":
                    sinistreService.changerStatut(sinistreId, "REFUSE", commentaire);
                    break;
                case "INDEMNISER":
                    String montantStr = req.getParameter("montantIndemnise");
                    if (montantStr == null || montantStr.isEmpty()) {
                        throw new Exception("Le montant de l'indemnisation est obligatoire.");
                    }
                    BigDecimal montant = new BigDecimal(montantStr);
                    sinistreService.indemniserSinistre(sinistreId, montant);
                    break;
                default:
                    throw new Exception("Action inconnue.");
            }

            resp.sendRedirect(req.getContextPath() + "/agent/sinistres?success=true");

        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/agent/sinistres?error=" + e.getMessage());
        }
    }
}