package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class PaiementDao {

    // Enregistrer un nouveau paiement en base de données
    public void save(Paiement paiement) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(paiement);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    // Mettre à jour un paiement existant (ex: valider une échéance en EFFECTUE)
    public void update(Paiement paiement) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(paiement);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    // Trouver un paiement par son ID
    public Paiement findById(int idPaiement) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Paiement.class, idPaiement);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Récupérer tous les paiements (triés du plus récent au plus ancien)
    public List<Paiement> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Paiement ORDER BY datePaiement DESC", Paiement.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Récupérer les paiements d'un contrat donné
    public List<Paiement> findByContrat(int idContrat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Paiement> query = session.createQuery(
                    "FROM Paiement WHERE idContrat = :idContrat ORDER BY datePaiement DESC", Paiement.class);
            query.setParameter("idContrat", idContrat);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Récupérer les paiements par statut (ex: EFFECTUE ou EN_ATTENTE)
    public List<Paiement> findByStatut(String statut) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Paiement> query = session.createQuery(
                    "FROM Paiement WHERE statut = :statut ORDER BY datePaiement DESC", Paiement.class);
            query.setParameter("statut", statut);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Supprimer un paiement par son identifiant
    public void delete(int idPaiement) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Paiement p = session.get(Paiement.class, idPaiement);
            if (p != null) {
                session.remove(p);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }
}
