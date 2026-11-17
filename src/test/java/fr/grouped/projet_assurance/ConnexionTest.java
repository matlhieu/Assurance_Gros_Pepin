package fr.grouped.projet_assurance;


import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnexionTest {

    @Test
    void sessionFactoryEstInitialisee() {
        assertNotNull(HibernateUtil.getSessionFactory(),
            "SessionFactory null → vérifier hibernate.cfg.xml");
    }

    @Test
    void sessionPeutEtreOuverte() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            assertTrue(session.isConnected(),
                "Session non connectée → vérifier MySQL + config");
        }
    }
}