package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ContratDAOImpl extends GenericDAOImpl<Contrat, Integer> implements ContratDAO {

    public ContratDAOImpl() {
        super(Contrat.class);
    }

    @Override
    public List<Contrat> findByClient(Integer idClient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Contrat> query = session.createQuery(
                "FROM Contrat c WHERE c.utilisateur.idUtilisateur = :idClient", Contrat.class);
            query.setParameter("idClient", idClient);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur findByClient Contrat", e);
        }
    }

    @Override
    public List<Contrat> findByStatut(StatutContrat statut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Contrat> query = session.createQuery(
                "FROM Contrat c WHERE c.statut = :statut", Contrat.class);
            query.setParameter("statut", statut);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur findByStatut Contrat", e);
        }
    }

    @Override
    public List<Contrat> search(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Contrat> query = session.createQuery(
                "FROM Contrat c WHERE c.numContrat LIKE :kw OR c.utilisateur.nom LIKE :kw", Contrat.class);
            query.setParameter("kw", "%" + keyword + "%");
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur search Contrat", e);
        }
    }

    // ---------- Requêtes avec JOIN FETCH (pour l'affichage dans les JSP) ----------

    private static final String SELECT_DETAILS =
        "SELECT DISTINCT c FROM Contrat c "
      + "JOIN FETCH c.utilisateur "
      + "JOIN FETCH c.typeAssurance "
      + "LEFT JOIN FETCH c.garanties ";

    @Override
    public List<Contrat> findAllAvecDetails() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(SELECT_DETAILS + "ORDER BY c.dateDebut DESC", Contrat.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur findAllAvecDetails Contrat", e);
        }
    }

    @Override
    public List<Contrat> findByClientAvecDetails(Integer idClient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    SELECT_DETAILS + "WHERE c.utilisateur.idUtilisateur = :idClient ORDER BY c.dateDebut DESC",
                    Contrat.class)
                .setParameter("idClient", idClient)
                .list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur findByClientAvecDetails Contrat", e);
        }
    }

    @Override
    public Contrat findByIdAvecDetails(Integer idContrat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(SELECT_DETAILS + "WHERE c.idContrat = :id", Contrat.class)
                .setParameter("id", idContrat)
                .uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Erreur findByIdAvecDetails Contrat", e);
        }
    }

    @Override
    public int expirerContratsEchus() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            try {
                int n = session.createMutationQuery(
                        "UPDATE Contrat c SET c.statut = :expire WHERE c.statut = :actif AND c.dateFin < :aujourdhui")
                    .setParameter("expire", StatutContrat.EXPIRE)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .setParameter("aujourdhui", java.time.LocalDate.now())
                    .executeUpdate();
                tx.commit();
                return n;
            } catch (RuntimeException e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
