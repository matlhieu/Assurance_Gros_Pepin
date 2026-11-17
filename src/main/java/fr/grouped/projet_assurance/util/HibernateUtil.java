package fr.grouped.projet_assurance.util;

import fr.grouped.projet_assurance.model.Paiement;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            // Sécurité : on enregistre explicitement la classe annotée Paiement
            configuration.addAnnotatedClass(Paiement.class);
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Erreur creation SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
