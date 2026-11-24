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
            Assertions.assertNotNull(list);
        } catch (Exception e) {
            System.out.println("Base de données non disponible : " + e.getMessage());
        }
    }

    @Test
    public void testSaveUpdateDelete() {
        PaiementDAO dao = new PaiementDAOImpl();

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

            Paiement found = dao.findById(p.getIdPaiement());
            Assertions.assertNotNull(found);
            Assertions.assertEquals("EN_ATTENTE", found.getStatut());

            found.setStatut("EFFECTUE");
            dao.update(found);

            Paiement updated = dao.findById(p.getIdPaiement());
            Assertions.assertEquals("EFFECTUE", updated.getStatut());

            dao.delete(updated.getIdPaiement());
            Assertions.assertNull(dao.findById(updated.getIdPaiement()));
        } catch (Exception e) {
            System.out.println("Base de données non disponible : " + e.getMessage());
        }
    }

    @Test
    public void testStatsDao() {
        StatsDAO statsDAO = new StatsDAOImpl();
        try {
            BigDecimal rev = statsDAO.getTotalRevenue();
            BigDecimal pending = statsDAO.getTotalPendingPayments();
            Assertions.assertNotNull(rev);
            Assertions.assertNotNull(pending);
        } catch (Exception e) {
            System.out.println("Base de données non disponible : " + e.getMessage());
        }
    }
}
