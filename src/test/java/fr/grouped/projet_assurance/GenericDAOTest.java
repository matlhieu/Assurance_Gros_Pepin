package fr.grouped.projet_assurance;

import fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl;
import fr.grouped.projet_assurance.model.Utilisateur;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenericDAOTest {

    private static UtilisateurDAOImpl dao;
    private static int idCree;

    @BeforeAll
    static void init() {
        dao = new UtilisateurDAOImpl();
    }

    @Test
    @Order(1)
    void saveInsereEnBase() {
        Utilisateur u = new Utilisateur();
        u.setEmail("test-" + System.currentTimeMillis() + "@test.fr");
        u.setMotDePasse("$2a$12$fakehashforTestOnly1234567890abcdefghij");
        u.setNom("Test");
        u.setPrenom("JUnit");
        u.setRole("CLIENT");

        dao.save(u);
        assertTrue(u.getIdUtilisateur() > 0,
            "L'id doit être généré (IDENTITY) après save");

        idCree = u.getIdUtilisateur();
        System.out.println("✅ Utilisateur inséré avec id = " + idCree);
    }

    @Test
    @Order(2)
    void findByIdRetourneLUtilisateur() {
        assertTrue(idCree > 0, "Le test 1 doit avoir réussi");

        Utilisateur u = dao.findById(idCree);
        assertNotNull(u, "findById doit retourner l'utilisateur");
        assertEquals("Test", u.getNom());
        assertEquals("CLIENT", u.getRole());
        System.out.println("✅ findById OK pour id = " + idCree);
    }

    @Test
    @Order(3)
    void findAllRetourneLesUtilisateurs() {
        List<Utilisateur> tous = dao.findAll();
        assertNotNull(tous);
        assertFalse(tous.isEmpty(), "findAll ne doit pas être vide");
        System.out.println("✅ findAll : " + tous.size() + " utilisateurs");
    }

    @Test
    @Order(4)
    void updateModifieEnBase() {
        Utilisateur u = dao.findById(idCree);
        u.setNom("Modifie");
        dao.update(u);

        Utilisateur relu = dao.findById(idCree);
        assertEquals("Modifie", relu.getNom());
        System.out.println("✅ update OK");
    }

    @Test
    @Order(5)
    void deleteSupprimeEnBase() {
        dao.delete(idCree);
        assertNull(dao.findById(idCree), "Après delete, findById doit être null");
        System.out.println("✅ delete OK");
    }
}
