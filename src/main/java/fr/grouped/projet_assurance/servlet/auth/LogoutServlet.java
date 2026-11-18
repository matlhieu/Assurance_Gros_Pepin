package fr.grouped.projet_assurance.servlet.auth;



import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;


@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 1. Récupérer la session sans la créer si elle n'existe pas
        HttpSession session = request.getSession(false);

        // 2. Invalider la session (supprime tous les attributs)
        if (session != null) {
            session.invalidate();
        }

        // 3. Rediriger vers la page de login
        response.sendRedirect(request.getContextPath() + "/login");
    }

    /**
     * Un POST est aussi accepté (par sécurité, si un formulaire
     * de déconnexion est utilisé au lieu d'un simple lien).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }
}