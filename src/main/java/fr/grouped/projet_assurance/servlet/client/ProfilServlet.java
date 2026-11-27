package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.PasswordUtil;
import fr.grouped.projet_assurance.util.Validator;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Profil de l'utilisateur connecté (client, agent ou admin) :
 *  POST action=infos       -> prénom, nom, e-mail
 *  POST action=motDePasse  -> changement de mot de passe (ancien mot de passe obligatoire)
 */
@WebServlet({"/client/profil", "/agent/profil", "/admin/profil"})
public class ProfilServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.consommerFlash(req);
        afficher(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Utilisateur session = WebUtil.utilisateurConnecte(req);
        // On repart toujours de la base : jamais de l'objet en session, ni d'un id envoyé par le formulaire
        Utilisateur u = utilisateurDAO.findById(session.getIdUtilisateur());
        if (u == null) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        Map<String, String> erreurs = new LinkedHashMap<>();

        if ("infos".equals(action)) {
            String prenom = nettoyer(req.getParameter("prenom"));
            String nom = nettoyer(req.getParameter("nom"));
            String email = nettoyer(req.getParameter("email")).toLowerCase(Locale.ROOT);

            if (!Validator.isNonVide(prenom) || prenom.length() > 50) erreurs.put("prenom", "Indiquez votre prénom (50 caractères maximum).");
            if (!Validator.isNonVide(nom) || nom.length() > 50) erreurs.put("nom", "Indiquez votre nom (50 caractères maximum).");
            if (!Validator.isEmailValide(email) || email.length() > 100) {
                erreurs.put("email", "Adresse e-mail invalide.");
            } else {
                Utilisateur existant = utilisateurDAO.findByEmail(email);
                if (existant != null && existant.getIdUtilisateur() != u.getIdUtilisateur()) {
                    erreurs.put("email", "Cette adresse est déjà utilisée par un autre compte.");
                }
            }

            if (erreurs.isEmpty()) {
                u.setPrenom(prenom);
                u.setNom(nom);
                u.setEmail(email);
                utilisateurDAO.update(u);
                req.getSession().setAttribute("utilisateur", u);   // le header affiche les nouvelles infos
                WebUtil.flash(req, "succes", "Vos informations ont bien été mises à jour.");
                resp.sendRedirect(req.getContextPath() + req.getServletPath());
                return;
            }

        } else if ("motDePasse".equals(action)) {
            String actuel = req.getParameter("motDePasseActuel");
            String nouveau = req.getParameter("nouveauMotDePasse");
            String confirmation = req.getParameter("confirmation");

            if (!PasswordUtil.check(actuel, u.getMotDePasse())) {
                erreurs.put("motDePasseActuel", "Mot de passe actuel incorrect.");
            }
            if (!Validator.isMotDePasseValide(nouveau)) {
                erreurs.put("nouveauMotDePasse", "Le nouveau mot de passe doit contenir au moins 6 caractères.");
            } else if (nouveau.equals(actuel)) {
                erreurs.put("nouveauMotDePasse", "Le nouveau mot de passe doit être différent de l'actuel.");
            }
            if (nouveau == null || !nouveau.equals(confirmation)) {
                erreurs.put("confirmation", "Les deux mots de passe ne correspondent pas.");
            }

            if (erreurs.isEmpty()) {
                u.setMotDePasse(PasswordUtil.hash(nouveau));
                utilisateurDAO.update(u);
                req.getSession().setAttribute("utilisateur", u);
                req.changeSessionId();   // nouvel identifiant de session après un changement d'identifiants
                WebUtil.flash(req, "succes", "Votre mot de passe a bien été modifié.");
                resp.sendRedirect(req.getContextPath() + req.getServletPath());
                return;
            }
            req.setAttribute("ongletMotDePasse", true);

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        req.setAttribute("erreurs", erreurs);
        afficher(req, resp);
    }

    private void afficher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Données à jour depuis la base (et non la copie en session)
        Utilisateur u = utilisateurDAO.findById(WebUtil.utilisateurConnecte(req).getIdUtilisateur());
        req.setAttribute("profil", u);
        // Liens propres à l'espace courant : /client/..., /agent/... ou /admin/...
        String espace = req.getServletPath().replace("/profil", "");
        req.setAttribute("baseUrl", req.getContextPath() + req.getServletPath());
        req.setAttribute("accueilUrl", req.getContextPath() + espace + "/dashboard");
        req.getRequestDispatcher("/WEB-INF/views/commun/profil.jsp").forward(req, resp);
    }

    private static String nettoyer(String s) {
        return s == null ? "" : s.trim();
    }
}
