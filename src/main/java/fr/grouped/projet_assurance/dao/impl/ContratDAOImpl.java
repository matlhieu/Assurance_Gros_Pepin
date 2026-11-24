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
}