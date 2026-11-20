package fr.grouped.projet_assurance.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordSeeder {
    public static void main(String[] args) {
        System.out.println("=== GÉNÉRATION DES HASH ===\n");

        String[][] users = {
            {"projetadmin@assurance.fr", "admin123"},
            {"agent@assurance.fr", "agent123"},
            {"robbstark@gmail.com", "KingInTheNorth"},
            {"daenerys@gmail.com", "Dracaris"}
        };

        for (String[] u : users) {
            String hash = BCrypt.hashpw(u[1], BCrypt.gensalt(12));
            System.out.println("-- " + u[0]);
            System.out.println("UPDATE utilisateurs SET mot_de_passe = '" + hash + "' WHERE email = '" + u[0] + "';");
            System.out.println();
        }
    }
}