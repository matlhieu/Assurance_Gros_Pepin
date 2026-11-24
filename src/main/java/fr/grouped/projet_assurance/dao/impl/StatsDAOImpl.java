package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.StatsDAO;
import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;

public class StatsDAOImpl implements StatsDAO {

    @Override
    public BigDecimal getTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                "SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'EFFECTUE'",
                BigDecimal.class
            ).uniqueResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getTotalPendingPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                "SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'EN_ATTENTE'",
                BigDecimal.class
            ).uniqueResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getTotalIndemnities() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                "SELECT SUM(s.montantIndemnise) FROM Sinistre s WHERE s.statut = 'INDEMNISE'",
                BigDecimal.class
            ).uniqueResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<Object[]> getContractsByStatus() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT c.statut, COUNT(c) FROM Contrat c GROUP BY c.statut",
                Object[].class
            ).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
