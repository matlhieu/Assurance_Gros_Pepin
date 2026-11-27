package fr.grouped.projet_assurance.servlet.admin;

import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.Validator;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestion des utilisateurs et de leurs rôles, réservée à l'administrateur (§2.1 du cahier des charges).
 *  GET  ?action=list[&role=..&q=..]  liste filtrable par rôle et par recherche
 *  GET  ?action=form[&id=..]        création / modification
 *  GET  ?action=delete&id=..        suppression (impossible sur soi-même)
 *  POST action=save                 enregistrement (mot de passe hashé)
 */
@WebServlet("/admin/utilisateurs")
public class UtilisateurAdminServlet extends HttpServlet {

    private static final List<String> ROLES = List.of("CLIENT", "AGENT", "ADMIN");

    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WebUtil.consommerFlash(request);
        String base = request.getContextPath() + request.getServletPath();
        request.setAttribute("baseUrl", base);
        request.setAttribute("idConnecte", WebUtil.utilisateurConnecte(request).getIdUtilisateur());
        request.setAttribute("roles", ROLES);

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "form" -> {
                Integer id = WebUtil.parseId(request.getParameter("id"));
                if (id != null) {
                    Utilisateur u = utilisateurDAO.findById(id);
                    if (u == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                    request.setAttribute("utilisateur", u);
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs/form.jsp")
                        .forward(request, response);
            }
            case "delete" -> {
                Integer id = WebUtil.parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

                if (id == WebUtil.utilisateurConnecte(request).getIdUtilisateur()) {
                    WebUtil.flash(request, "erreur", "Vous ne pouvez pas supprimer votre propre compte.");
                } else {
                    try {
                        utilisateurDAO.delete(id);
                        WebUtil.flash(request, "succes", "Utilisateur supprimé.");
                    } catch (RuntimeException e) {
                        // Utilisateur référencé par des contrats / sinistres / paiements (clé étrangère)
                        WebUtil.flash(request, "erreur",
                                "Cet utilisateur possède des contrats ou des sinistres : il ne peut pas être supprimé.");
                    }
                }
                response.sendRedirect(base);
            }
            default -> afficherListe(request, response, base);
        }
    }

    private void afficherListe(HttpServletRequest request, HttpServletResponse response, String base)
            throws ServletException, IOException {

        List<Utilisateur> tous = utilisateurDAO.findAll();

        // Compteurs par rôle pour les onglets (0 par défaut pour chaque rôle connu)
        Map<String, Integer> compteurs = new LinkedHashMap<>();
        ROLES.forEach(r -> compteurs.put(r, 0));
        tous.forEach(u -> {
            String r = (u.getRole() == null) ? "AUTRE" : u.getRole();
            compteurs.merge(r, 1, Integer::sum);
        });

        // Filtres : rôle (onglet) et recherche texte
        // Attention : List.of(...) est immuable, contains(null) lève une NullPointerException
        String roleParam = request.getParameter("role");
        String filtreRole = (roleParam != null && ROLES.contains(roleParam)) ? roleParam : null;
        String q = request.getParameter("q") == null ? "" : request.getParameter("q").trim().toLowerCase();

        List<Utilisateur> utilisateurs = new ArrayList<>(tous);
        if (filtreRole != null) {
            utilisateurs.removeIf(u -> !filtreRole.equals(u.getRole()));
        }
        if (!q.isEmpty()) {
            utilisateurs.removeIf(u ->
                    !nomComplet(u).toLowerCase().contains(q)
                    && !u.getEmail().toLowerCase().contains(q));
        }

        // Tri : rôle (ADMIN puis AGENT puis CLIENT) puis nom puis prénom
        utilisateurs.sort((a, b) -> {
            int r = Integer.compare(poidsRole(a.getRole()), poidsRole(b.getRole()));
            if (r != 0) return r;
            r = a.getNom() == null ? 0 : a.getNom().compareToIgnoreCase(b.getNom());
            if (r != 0) return r;
            return a.getPrenom() == null ? 0 : a.getPrenom().compareToIgnoreCase(b.getPrenom());
        });

        request.setAttribute("compteurs", compteurs);
        request.setAttribute("nbTotal", tous.size());
        request.setAttribute("filtreRole", filtreRole);
        request.setAttribute("utilisateurs", utilisateurs);
        request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs/liste.jsp")
                .forward(request, response);
    }

    private static int poidsRole(String role) {
        return switch (role == null ? "" : role) { case "ADMIN" -> 0; case "AGENT" -> 1; default -> 2; };
    }

    private static String nomComplet(Utilisateur u) {
        return (u.getPrenom() == null ? "" : u.getPrenom()) + " " + (u.getNom() == null ? "" : u.getNom());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String base = request.getContextPath() + request.getServletPath();

        Integer id = WebUtil.parseId(request.getParameter("id"));
        String nom = clean(request.getParameter("nom"));
        String prenom = clean(request.getParameter("prenom"));
        String email = clean(request.getParameter("email"));
        String role = request.getParameter("role");
        String motDePasse = request.getParameter("motDePasse") == null ? "" : request.getParameter("motDePasse");

        Map<String, String> erreurs = new LinkedHashMap<>();

        if (!Validator.isNonVide(nom))      erreurs.put("nom", "Le nom est obligatoire.");
        if (!Validator.isNonVide(prenom))   erreurs.put("prenom", "Le prénom est obligatoire.");
        if (!Validator.isEmailValide(email)) erreurs.put("email", "L'adresse e-mail n'est pas valide.");

        // E-mail unique : occupé par un autre compte que celui modifié ?
        if (!erreurs.containsKey("email")) {
            Utilisateur existant = utilisateurDAO.findByEmail(email);
            int idModifie = (id == null) ? -1 : id;
            if (existant != null && existant.getIdUtilisateur() != idModifie) {
                erreurs.put("email", "Cet e-mail est déjà utilisé par un autre compte.");
            }
        }

        if (role == null || !ROLES.contains(role)) erreurs.put("role", "Choisissez un rôle valide.");

        boolean creation = (id == null);
        if (creation && motDePasse.isBlank()) {
            erreurs.put("motDePasse", "Un mot de passe est obligatoire à la création du compte.");
        } else if (!motDePasse.isBlank() && !Validator.isMotDePasseValide(motDePasse)) {
            erreurs.put("motDePasse", "Le mot de passe doit contenir au moins 6 caractères.");
        }

        // Ne pas pouvoir se retirer son propre rôle ADMIN (sinon l'administrateur se bloque lui-même)
        Utilisateur connecte = WebUtil.utilisateurConnecte(request);
        if (!erreurs.containsKey("role") && !creation
                && id == connecte.getIdUtilisateur() && !"ADMIN".equals(role)) {
            erreurs.put("role", "Vous ne pouvez pas retirer votre propre rôle d'administrateur.");
        }

        if (!erreurs.isEmpty()) {
            // Réaffiche le formulaire avec les erreurs et les valeurs saisies (voir form.jsp)
            if (id != null) request.setAttribute("utilisateur", utilisateurDAO.findById(id));
            request.setAttribute("erreurs", erreurs);
            request.getRequestDispatcher("/WEB-INF/views/admin/utilisateurs/form.jsp")
                    .forward(request, response);
            return;
        }

        Utilisateur u = creation ? new Utilisateur() : utilisateurDAO.findById(id);
        if (u == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setRole(role);
        if (!motDePasse.isBlank()) {
            u.setMotDePasse(fr.grouped.projet_assurance.util.PasswordUtil.hash(motDePasse));
        }

        try {
            if (creation) utilisateurDAO.save(u); else utilisateurDAO.update(u);
            WebUtil.flash(request, "succes",
                    creation ? "Utilisateur créé avec le rôle " + role + "." : "Utilisateur mis à jour.");
        } catch (RuntimeException e) {
            WebUtil.flash(request, "erreur", "Enregistrement impossible : " + e.getMessage());
        }

        // Si l'administrateur vient de modifier son propre compte, rafraîchit la session
        if (!creation && u.getIdUtilisateur() == connecte.getIdUtilisateur()) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", u);
            session.setAttribute("role", u.getRole());
        }

        response.sendRedirect(base);
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }
}
