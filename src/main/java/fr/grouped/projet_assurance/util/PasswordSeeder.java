package fr.grouped.projet_assurance.util;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordSeeder {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "password123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Mot de passe : " + password);
        System.out.println("Hash         : " + hash);
        System.out.println();
        System.out.println("Copiez dans seed.sql :");
        System.out.println("UPDATE utilisateurs SET mot_de_passe = '"
                           + hash + "' WHERE email LIKE '%@assurance.fr';");
    }
}