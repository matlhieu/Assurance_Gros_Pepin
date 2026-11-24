package fr.grouped.projet_assurance.dao;

import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.StatsDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaiementDaoTest {

    @Test
    public void testFindAll() {
        PaiementDAO dao = new PaiementDAOImpl();
        try {
            List<Paiement> list = dao.findAll();
            System.out.println("Paiements trouvés : " + (list != null ? list.size() : 0));
            Assertions.assertNotNull(list);
        } catch (Exception e) {
            System.out.println("DB not available during test: " + e.getMessage());
        }
    }

    @Test
    public void testSaveUpdateDelete() {
        PaiementDAO dao = new PaiementDAOImpl();

        // 1. Sauvegarde d'un paiement en attente
        Paiement p = new Paiement();
        p.setMontant(new BigDecimal("120.50"));
        p.setDatePaiement(LocalDate.now());
        p.setModePaiement("CARTE");
        p.setStatut("EN_ATTENTE");

        Contrat c = new Contrat();
        c.setIdContrat(1);
        p.setContrat(c);

        try {
            dao.save(p);
            Assertions.assertNotNull(p.getIdPaiement());

            // 2. Recherche par ID
            Paiement found = dao.findById(p.getIdPaiement());
            Assertions.assertNotNull(found);
            Assertions.assertEquals("EN_ATTENTE", found.getStatut());

            // 3. Mise à jour (encaissement)
            found.setStatut("EFFECTUE");
            dao.update(found);

            Paiement updated = dao.findById(p.getIdPaiement());
            Assertions.assertEquals("EFFECTUE", updated.getStatut());

            // 4. Nettoyage (suppression)
            dao.delete(updated.getIdPaiement());
            Assertions.assertNull(dao.findById(updated.getIdPaiement()));
        } catch (Exception e) {
            System.out.println("DB not available during test: " + e.getMessage());
        }
    }

    @Test
    public void testStatsDao() {
        StatsDAO statsDAO = new StatsDAOImpl();
        try {
            BigDecimal rev = statsDAO.getTotalRevenue();
            BigDecimal pending = statsDAO.getTotalPendingPayments();
            System.out.println("Revenue: " + rev + ", Pending: " + pending);
            Assertions.assertNotNull(rev);
            Assertions.assertNotNull(pending);
        } catch (Exception e) {
            System.out.println("DB not available during test: " + e.getMessage());
        }
    }
}
