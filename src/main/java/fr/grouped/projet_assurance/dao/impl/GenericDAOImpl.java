package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.GenericDAO;
import fr.grouped.projet_assurance.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

public abstract class GenericDAOImpl<T, ID extends Serializable>
        implements GenericDAO<T, ID> {

    protected final Class<T> entityClass;

    protected GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erreur save " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erreur update " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void delete(ID id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) session.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erreur delete " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public T findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(entityClass, id);
        }
    }

    /**
     * Find all entities of the type T.
     * @return A list of all entities.
     */
    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "from " + entityClass.getSimpleName(), entityClass).list();
        }
    }
}
