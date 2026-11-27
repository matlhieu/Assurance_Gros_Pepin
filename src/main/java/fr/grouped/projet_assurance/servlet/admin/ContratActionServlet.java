package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Cycle de vie d'un contrat (agent et admin) : activer, suspendre, résilier, renouveler.
 * En POST uniquement : une action qui modifie des données ne doit pas passer par un simple lien.
 */
@WebServlet({"/admin/contrats/action", "/agent/contrats/action"})
public class ContratActionServlet extends HttpServlet {

    private ContratDAO contratDAO;
    private final fr.grouped.projet_assurance.service.PaiementService paiementService =
            new fr.grouped.projet_assurance.service.PaiementService();

    @Override
    public void init() throws ServletException {
        contratDAO = new ContratDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // /agent/contrats/action -> /agent/contrats
        String base = request.getContextPath() + request.getServletPath().replace("/action", "");
        Integer id = WebUtil.parseId(request.getParameter("id"));
        String action = request.getParameter("action");
        Contrat contrat = (id == null) ? null : contratDAO.findById(id);

        if (contrat == null || action == null) {
            response.sendRedirect(base);
            return;
        }

        switch (action) {
            case "activer" -> {
                contrat.setStatut(StatutContrat.ACTIF);
                contratDAO.update(contrat);
                LocalDate debut = contrat.getDateDebut().isAfter(LocalDate.now()) ? contrat.getDateDebut() : LocalDate.now();
                int n = paiementService.genererEcheancier(contrat, ContratServlet.nbEcheances(request), debut);
                WebUtil.flash(request, "succes", "Contrat activé : le client est couvert."
                        + (n > 0 ? " " + n + " échéance(s) de paiement générée(s)." : ""));
                response.sendRedirect(base + "?action=detail&id=" + id);
                return;
            }
            case "suspendre" -> {
                contrat.setStatut(StatutContrat.SUSPENDU);
                WebUtil.flash(request, "succes", "Contrat suspendu.");
            }
            case "resilier" -> {
                contrat.setStatut(StatutContrat.RESILIE);
                WebUtil.flash(request, "succes", "Contrat résilié.");
            }
            case "renouveler" -> {
                LocalDate debutNouvellePeriode = contrat.getDateFin();
                contrat.setStatut(StatutContrat.ACTIF);
                contrat.setDateFin(contrat.getDateFin().plusYears(1));
                contratDAO.update(contrat);
                int n = paiementService.genererEcheancier(contrat, ContratServlet.nbEcheances(request),
                        debutNouvellePeriode.isAfter(LocalDate.now()) ? debutNouvellePeriode : LocalDate.now());
                WebUtil.flash(request, "succes", "Contrat renouvelé jusqu'au " + WebUtil.date(contrat.getDateFin()) + "."
                        + (n > 0 ? " " + n + " échéance(s) générée(s)." : ""));
                response.sendRedirect(base + "?action=detail&id=" + id);
                return;
            }
            default -> {
                response.sendRedirect(base + "?action=detail&id=" + id);
                return;
            }
        }
        contratDAO.update(contrat);
        response.sendRedirect(base + "?action=detail&id=" + id);
    }
}
