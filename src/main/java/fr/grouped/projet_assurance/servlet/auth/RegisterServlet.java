package fr.grouped.projet_assurance.servlet.auth;

import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.service.AuthService;
import fr.grouped.projet_assurance.util.Validator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    /**
     * Affiche le formulaire d'inscription.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si l'utilisateur est déjà connecté, on le redirige
        if (request.getSession(false) != null
                && request.getSession(false).getAttribute("utilisateur") != null) {
            response.sendRedirect(request.getContextPath() + "/client/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
               .forward(request, response);
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupérer les paramètres
        String nom          = request.getParameter("nom");
        String prenom       = request.getParameter("prenom");
        String email        = request.getParameter("email");
        String telephone    = request.getParameter("telephone");
        String adresse      = request.getParameter("adresse");
        String motDePasse   = request.getParameter("motDePasse");
        String confirmation = request.getParameter("confirmation");

        // 2. Valider côté serveur — le PDF impose la validation dans les servlets
        Map<String, String> erreurs = new HashMap<>();

        if (!Validator.isNonVide(nom)) {
            erreurs.put("nom", "Le nom est obligatoire.");
        } else if (nom.length() > 50) {
            erreurs.put("nom", "Le nom ne doit pas dépasser 50 caractères.");
        }

        if (!Validator.isNonVide(prenom)) {
            erreurs.put("prenom", "Le prénom est obligatoire.");
        } else if (prenom.length() > 50) {
            erreurs.put("prenom", "Le prénom ne doit pas dépasser 50 caractères.");
        }

        if (!Validator.isNonVide(email)) {
            erreurs.put("email", "L'email est obligatoire.");
        } else if (!Validator.isEmailValide(email)) {
            erreurs.put("email", "Format d'email invalide.");
        }

        if (!Validator.isNonVide(telephone)) {
            erreurs.put("telephone", "Le téléphone est obligatoire.");
        } else if (!Validator.isTelephoneValide(telephone)) {
            erreurs.put("telephone", "Format de téléphone invalide.");
        }

        if (!Validator.isNonVide(adresse)) {
            erreurs.put("adresse", "L'adresse est obligatoire.");
        }

        if (!Validator.isNonVide(motDePasse)) {
            erreurs.put("motDePasse", "Le mot de passe est obligatoire.");
        } else if (!Validator.isMotDePasseValide(motDePasse)) {
            erreurs.put("motDePasse", "Le mot de passe doit contenir au moins 6 caractères.");
        }

        if (!motDePasse.equals(confirmation)) {
            erreurs.put("confirmation", "Les mots de passe ne correspondent pas.");
        }

        // 3. Si des erreurs → re-afficher le formulaire avec les valeurs saisies
        if (!erreurs.isEmpty()) {
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("email", email);
            request.setAttribute("telephone", telephone);
            request.setAttribute("adresse", adresse);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                   .forward(request, response);
            return;
        }

        // 4. Tenter l'inscription
        try {
            Utilisateur utilisateur = authService.inscrire(
                    nom, prenom, email, motDePasse);

            // Inscription réussie → rediriger vers login avec message
            request.getSession().setAttribute("flashSuccess",
                    "Compte créé avec succès. Vous pouvez maintenant vous connecter.");
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (IllegalArgumentException e) {
            // Email déjà utilisé, ou autre erreur métier
            erreurs.put("email", e.getMessage());
            request.setAttribute("erreurs", erreurs);
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                   .forward(request, response);
        }
    }
}