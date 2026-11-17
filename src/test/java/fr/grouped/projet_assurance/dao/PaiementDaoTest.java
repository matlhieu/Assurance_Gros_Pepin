package fr.grouped.projet_assurance.dao;

import fr.grouped.projet_assurance.dao.impl.PaiementDao;
import fr.grouped.projet_assurance.dao.impl.StatsDao;
import fr.grouped.projet_assurance.model.Paiement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaiementDaoTest {

    @Test
    public void testFindAll() {
        PaiementDao dao = new PaiementDao();
        List<Paiement> list = dao.findAll();
        System.out.println("Paiements trouvés : " + list.size());
        Assertions.assertNotNull(list);
    }

    @Test
    public void testSaveUpdateDelete() {
        PaiementDao dao = new PaiementDao();

        // 1. Sauvegarde d'un paiement en attente
        Paiement p = new Paiement(
                new BigDecimal("120.50"),
                LocalDate.now(),
                "CARTE",
                "EN_ATTENTE",
                1
        );
        dao.save(p);
        Assertions.assertTrue(p.getIdPaiement() > 0, "L'id généré doit être > 0");

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
    }

    @Test
    public void testStatsDao() {
        StatsDao statsDao = new StatsDao();
        double rev = statsDao.getTotalRevenue();
        double pending = statsDao.getTotalPendingPayments();
        System.out.println("Revenue: " + rev + ", Pending: " + pending);
    }
}
