package fr.grouped.projet_assurance.util;


import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int ROUNDS = 12;   // coût BCrypt (10-12 recommandé)

    private PasswordUtil() {}

    /** Hash un mot de passe en clair */
    public static String hash(String motDePasseClair) {
        if (motDePasseClair == null || motDePasseClair.isEmpty()) {
            throw new IllegalArgumentException("Mot de passe vide");
        }
        return BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(ROUNDS));
    }

    /** Vérifie un mot de passe en clair contre un hash BCrypt */
    public static boolean check(String motDePasseClair, String hash) {
        if (motDePasseClair == null || hash == null) return false;
        try {
            return BCrypt.checkpw(motDePasseClair, hash);
        } catch (IllegalArgumentException e) {
            // hash mal formé
            return false;
        }
    }
}
