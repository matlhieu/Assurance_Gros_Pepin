package fr.grouped.projet_assurance.servlet.auth;

import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        // Validation simple côté serveur
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            req.setAttribute("erreur", "Email et mot de passe sont obligatoires.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Utilisateur u = authService.authentifier(email, motDePasse);
        if (u == null) {
            req.setAttribute("erreur", "Identifiants incorrects.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Stockage en session
        req.getSession().setAttribute("utilisateur", u);
        req.getSession().setAttribute("role", u.getRole());

        // Redirection selon le rôle
        switch (u.getRole()) {
            case "ADMIN"  -> resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            case "AGENT"  -> resp.sendRedirect(req.getContextPath() + "/agent/dashboard");
            default       -> resp.sendRedirect(req.getContextPath() + "/client/dashboard");
        }
    }
}
