package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;

public class StatsDao {

    public double getTotalRevenue() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                    "SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'EFFECTUE'",
                    BigDecimal.class
            ).uniqueResult();
            return total == null ? 0.0 : total.doubleValue();
        }
    }

    public double getTotalPendingPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total = session.createQuery(
                    "SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'EN_ATTENTE'",
                    BigDecimal.class
            ).uniqueResult();
            return total == null ? 0.0 : total.doubleValue();
        }
    }

    // need Sinistre entity
    public double getTotalIndemnities() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Double total = session.createQuery(
                "SELECT SUM(s.montantIndemnise) FROM Sinistre s WHERE s.statut = 'INDEMNISE'",
                Double.class
        ).uniqueResult();
        session.close();
        return total == null ? 0.0 : total;
    }

    // need contrat
    public List<Object[]> getContractsByStatus() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Object[]> list = session.createQuery(
                "SELECT c.statut, COUNT(c) FROM Contrat c GROUP BY c.statut",
                Object[].class
        ).list();
        session.close();
        return list;
    }

    //need Contrat & TypeAssurance
    public List<Object[]> getClientsByInsuranceType() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Object[]> list = session.createQuery(
                "SELECT c.typeAssurance.nom, COUNT(DISTINCT c.idClient) FROM Contrat c GROUP BY c.typeAssurance.nom",
                Object[].class
        ).list();
        session.close();
        return list;
    }
}