package fr.grouped.projet_assurance.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // On capture l'erreur complète
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            
            // On l'écrit dans un fichier à la racine du projet
            try {
                Files.write(Paths.get("erreur_hibernate.txt"), sw.toString().getBytes());
                System.err.println("\n\n❌ ERREUR HIBERNATE ! Regarde le fichier 'erreur_hibernate.txt' à la racine de ton projet.\n\n");
            } catch (Exception e) {
                System.err.println("Impossible d'écrire le fichier d'erreur : " + e.getMessage());
            }
            
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}