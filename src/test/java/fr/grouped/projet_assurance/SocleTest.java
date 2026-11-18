package fr.grouped.projet_assurance;

import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.HibernateUtil;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocleTest {

    @Test
    void sessionFactoryDemarre() {
        assertNotNull(HibernateUtil.getSessionFactory());
    }

    @Test
    void utilisateursDuSeedSontLisibles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer count = session
                .createQuery("select count(u) from Utilisateur u", Integer.class)
                .uniqueResult();
            assertNotNull(count);
            assertTrue(count > 0, "Le seed.sql doit contenir au moins 1 utilisateur");
        }
    }

    @Test
    void chargerAdminParId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Utilisateur u = session.get(Utilisateur.class, 1);
            assertNotNull(u);
            assertEquals("admin@assurance.fr", u.getEmail());
            assertEquals("ADMIN", u.getRole());
        }
    }
}