package fr.grouped.projet_assurance.dao.impl;

import java.util.List;

import org.hibernate.Session;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class PaiementDAOImpl extends GenericDAOImpl<Paiement, Integer> implements PaiementDAO {

    /**
     * Charge aussi le contrat, son client et son type : les JSP affichent p.contrat.numContrat,
     * ce qui provoquait une LazyInitializationException une fois la session fermée.
     */
    private static final String FETCH_CONTRAT =
        "SELECT p FROM Paiement p JOIN FETCH p.contrat c JOIN FETCH c.utilisateur JOIN FETCH c.typeAssurance ";

    public PaiementDAOImpl() {
        super(Paiement.class);
    }

    @Override
    public List<Paiement> findByContrat(Integer contratId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                FETCH_CONTRAT + "WHERE c.idContrat = :cid ORDER BY p.datePaiement DESC", 
                Paiement.class)
                .setParameter("cid", contratId)
                .getResultList();
        }
    }

    @Override
    public List<Paiement> findByClientId(Integer clientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                FETCH_CONTRAT + "WHERE c.utilisateur.idUtilisateur = :cid ORDER BY p.datePaiement DESC",
                Paiement.class)
                .setParameter("cid", clientId)
                .getResultList();
        }
    }

    @Override
    public List<Paiement> findEnAttente() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                FETCH_CONTRAT + "WHERE p.statut = 'EN_ATTENTE' ORDER BY p.datePaiement ASC", 
                Paiement.class)
                .getResultList();
        }
    }

    @Override
    public List<Paiement> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                FETCH_CONTRAT + "ORDER BY p.datePaiement DESC", 
                Paiement.class)
                .getResultList();
        }
    }
}
