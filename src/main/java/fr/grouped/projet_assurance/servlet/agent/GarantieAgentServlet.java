package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Garantie;
import fr.grouped.projet_assurance.model.TypeAssurance;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des garanties par l'agent (§2.4 du sujet) :
 *  GET  /agent/garanties[?modifier=id]       -> catalogue + formulaire (création ou modification)
 *  POST action=enregistrer (id?, nom, description)
 *  POST action=supprimer (id)                -> refusé si la garantie est utilisée par un contrat ou un produit
 */
@WebServlet("/agent/garanties")
public class GarantieAgentServlet extends HttpServlet {

    private final TypeAssuranceDAO typeAssuranceDAO = new TypeAssuranceDAOImpl();
    private final GarantieDAO garantieDAO = new GarantieDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.consommerFlash(req);
        Integer idModif = WebUtil.parseId(req.getParameter("modifier"));
        if (idModif != null) {
            req.setAttribute("garantieModifiee", garantieDAO.findById(idModif));
        }
        afficher(req, resp);
    }

    private void afficher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TypeAssurance> types = typeAssuranceDAO.findAllAvecGaranties();
        List<Contrat> contrats = contratDAO.findAllAvecDetails();

        Map<Integer, Long> actifs = new HashMap<>();     // contrats actifs qui incluent la garantie
        Map<Integer, Long> usages = new HashMap<>();     // tous contrats + produits (bloque la suppression)
        for (Contrat c : contrats) {
            for (Garantie g : c.getGaranties()) {
                usages.merge(g.getIdGarantie(), 1L, Long::sum);
                if (c.getStatut() == StatutContrat.ACTIF) actifs.merge(g.getIdGarantie(), 1L, Long::sum);
            }
        }
        for (TypeAssurance t : types) {
            for (Garantie g : t.getGarantiesDisponibles()) usages.merge(g.getIdGarantie(), 1L, Long::sum);
        }

        req.setAttribute("types", types);
        req.setAttribute("garanties", garantieDAO.findAll());
        req.setAttribute("utilisation", actifs);
        req.setAttribute("usages", usages);
        req.getRequestDispatcher("/WEB-INF/views/agent/garanties.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        Integer id = WebUtil.parseId(req.getParameter("id"));

        if ("supprimer".equals(action)) {
            try {
                if (id != null) garantieDAO.delete(id);
                WebUtil.flash(req, "succes", "Garantie supprimée.");
            } catch (RuntimeException e) {
                WebUtil.flash(req, "erreur", "Cette garantie est utilisée par un contrat ou un produit : retirez-la d'abord.");
            }
            resp.sendRedirect(req.getContextPath() + "/agent/garanties");
            return;
        }

        if (!"enregistrer".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String nom = req.getParameter("nom") == null ? "" : req.getParameter("nom").trim();
        String description = req.getParameter("description") == null ? "" : req.getParameter("description").trim();
        Map<String, String> erreurs = new LinkedHashMap<>();
        if (nom.isEmpty() || nom.length() > 50) erreurs.put("nom", "Nom obligatoire (50 caractères maximum).");
        if (description.length() > 500) erreurs.put("description", "Description trop longue (500 caractères maximum).");
        boolean doublon = garantieDAO.findAll().stream()
                .anyMatch(g -> g.getNom().equalsIgnoreCase(nom) && !g.getIdGarantie().equals(id));
        if (doublon) erreurs.put("nom", "Une garantie porte déjà ce nom.");

        if (!erreurs.isEmpty()) {
            req.setAttribute("erreurs", erreurs);
            if (id != null) req.setAttribute("garantieModifiee", garantieDAO.findById(id));
            afficher(req, resp);
            return;
        }

        Garantie g = (id == null) ? new Garantie() : garantieDAO.findById(id);
        if (g == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        g.setNom(nom);
        g.setDescription(description.isEmpty() ? null : description);
        if (id == null) {
            garantieDAO.save(g);
            WebUtil.flash(req, "succes", "Garantie « " + nom + " » créée. Vous pouvez l'ajouter à un contrat depuis sa fiche.");
        } else {
            garantieDAO.update(g);
            WebUtil.flash(req, "succes", "Garantie « " + nom + " » modifiée.");
        }
        resp.sendRedirect(req.getContextPath() + "/agent/garanties");
    }
}
