package fr.grouped.projet_assurance;

import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UtilisateurDAOTest {

    private static UtilisateurDAOImpl dao;
    private static final String EMAIL_TEST = "auth-test@assurance.fr";

    @BeforeAll
    static void init() {
        dao = new UtilisateurDAOImpl();
        // Nettoyage préalable si le test précédent a planté
        Utilisateur ancien = dao.findByEmail(EMAIL_TEST);
        if (ancien != null) dao.delete(ancien.getIdUtilisateur());
    }

    @AfterAll
    static void cleanup() {
        Utilisateur u = dao.findByEmail(EMAIL_TEST);
        if (u != null) dao.delete(u.getIdUtilisateur());
    }

    @Test
    @Order(1)
    void emailExistsFauxPourEmailInconnu() {
        assertFalse(dao.emailExists("inexistant-" + System.currentTimeMillis() + "@nope.fr"),
            "emailExists doit retourner false pour un email inconnu");
        System.out.println("✅ emailExists(false) OK");
    }

    @Test
    @Order(2)
    void savePuisEmailExistsVrai() {
        Utilisateur u = new Utilisateur();
        u.setEmail(EMAIL_TEST);
        u.setMotDePasse("$2a$12$fakehashfakehashfakehashfakehashfakehashfake");
        u.setNom("Auth");
        u.setPrenom("Test");
        u.setRole("CLIENT");
        dao.save(u);

        assertTrue(dao.emailExists(EMAIL_TEST),
            "emailExists doit retourner true après insert");
        System.out.println("✅ emailExists(true) OK");
    }

    @Test
    @Order(3)
    void findByEmailRetourneLeBonUtilisateur() {
        Utilisateur u = dao.findByEmail(EMAIL_TEST);
        assertNotNull(u, "findByEmail doit trouver l'utilisateur");
        assertEquals(EMAIL_TEST, u.getEmail());
        assertEquals("Auth", u.getNom());
        System.out.println("✅ findByEmail OK pour " + EMAIL_TEST);
    }

    @Test
    @Order(4)
    void findByEmailRetourneNullSiAbsent() {
        Utilisateur u = dao.findByEmail("vraiment-inexistant@nope.fr");
        assertNull(u, "findByEmail doit retourner null pour un email absent");
        System.out.println("✅ findByEmail(null) OK");
    }

    @Test
    @Order(5)
    void findByRoleRetourneLesClients() {
        List<Utilisateur> clients = dao.findByRole("CLIENT");
        assertNotNull(clients);
        assertFalse(clients.isEmpty(), "Il doit y avoir au moins 1 client (seed + test)");
        for (Utilisateur c : clients) {
            assertEquals("CLIENT", c.getRole());
        }
        System.out.println("✅ findByRole(CLIENT) : " + clients.size() + " clients");
    }
}