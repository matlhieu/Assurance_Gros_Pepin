package fr.grouped.projet_assurance.servlet.auth;

import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.PasswordUtil;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    // Pour les tests unitaires : injection
    public AuthService(UtilisateurDAO dao) {
        this.utilisateurDAO = dao;
    }

    /**
     * Inscrit un nouveau client.
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    public Utilisateur inscrire(String nom, String prenom, String email,
                                String telephone, String adresse,
                                String motDePasse) {
        if (utilisateurDAO.emailExists(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        // u.setTelephone(telephone);
        // u.setAdresse(adresse);
        u.setMotDePasse(PasswordUtil.hash(motDePasse));
        // u.setRole("CLIENT");
        // u.setDateInscription(java.time.LocalDate.now());
        // u.setStatutCompte("ACTIF");

        utilisateurDAO.save(u);
        return u;
    }

    /**
     * Authentifie un utilisateur.
     * @return l'utilisateur si email + mot de passe corrects, sinon null
     */
    public Utilisateur authentifier(String email, String motDePasse) {
        Utilisateur u = utilisateurDAO.findByEmail(email);
        if (u == null) return null;
        if (!PasswordUtil.check(motDePasse, u.getMotDePasse())) return null;
        return u;
    }
}