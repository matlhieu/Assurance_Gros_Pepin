package fr.grouped.projet_assurance.service;

import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.PasswordUtil;


public class AuthService {
    private UtilisateurDAO dao = new UtilisateurDAOImpl();

    /** Retourne l'utilisateur si email + mot de passe corrects, sinon null */
    public Utilisateur authentifier(String email, String motDePasse) {
        Utilisateur u = dao.findByEmail(email);
        if (u == null) return null;
        if (!PasswordUtil.check(motDePasse, u.getMotDePasse())) return null;
        return u;
    }

    /** Inscrit un nouveau client (rôle CLIENT par défaut) */
    public Utilisateur inscrire(String nom, String prenom,
                                String email, String motDePasse) {
        if (dao.emailExists(email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setMotDePasse(PasswordUtil.hash(motDePasse));
        u.setRole("CLIENT");
        dao.save(u);
        return u;
    }
}
