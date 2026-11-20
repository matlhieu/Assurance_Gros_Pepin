package fr.grouped.projet_assurance.filter;

import fr.grouped.projet_assurance.model.Utilisateur;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*") 
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/image/") || 
            path.equals("/") || path.equals("/login") || path.equals("/inscription")) {
            chain.doFilter(request, response);
            return;
        }

        
        HttpSession session = req.getSession(false);
        Utilisateur utilisateur = (session != null) ? (Utilisateur) session.getAttribute("utilisateur") : null;

        if (utilisateur == null) {
          
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

       
        String role = utilisateur.getRole();
        
        if (path.startsWith("/agent/") && !"AGENT".equals(role) && !"ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé : Vous n'êtes pas un agent.");
            return;
        }
        
        if (path.startsWith("/client/") && !"CLIENT".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé : Vous n'êtes pas un client.");
            return;
        }

      
        chain.doFilter(request, response);
    }
}