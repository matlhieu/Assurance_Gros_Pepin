package fr.grouped.projet_assurance.filter;

import fr.grouped.projet_assurance.model.Utilisateur;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Contrôle d'accès centralisé :
 *  - /client/*  : réservé aux CLIENT
 *  - /agent/*   : AGENT ou ADMIN
 *  - /admin/*   : ADMIN uniquement
 *  - /pieces-jointes : tout utilisateur connecté (les droits fins sont vérifiés dans la servlet)
 * Non connecté -> redirection vers /login ; mauvais rôle -> 403.
 */
@WebFilter(urlPatterns = {"/client/*", "/agent/*", "/admin/*", "/pieces-jointes"})
public class AuthFilter extends HttpFilter {

    private final fr.grouped.projet_assurance.dao.SinistreDAO sinistreDAO =
            new fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);
        Utilisateur u = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String chemin = req.getServletPath();
        String role = u.getRole();
        boolean autorise;
        if (chemin.startsWith("/client/")) {
            autorise = "CLIENT".equals(role);
        } else if (chemin.startsWith("/agent/")) {
            autorise = "AGENT".equals(role) || "ADMIN".equals(role);
        } else if (chemin.startsWith("/admin/")) {
            autorise = "ADMIN".equals(role);
        } else {
            autorise = true;
        }

        if (!autorise) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        // Badge du menu "Sinistres" : nombre de nouvelles déclarations à prendre en charge
        if ("AGENT".equals(role) || "ADMIN".equals(role)) {
            try {
                req.setAttribute("nbNouveauxSinistres", sinistreDAO.findByStatut("DECLARE").size());
            } catch (RuntimeException e) {
                // le badge est un confort : une erreur ici ne doit jamais bloquer la page
            }
        }

        // Pages privées : pas de mise en cache par le navigateur (bouton "retour" après déconnexion)
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(req, resp);
    }
}
