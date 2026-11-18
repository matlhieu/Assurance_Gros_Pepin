package fr.grouped.projet_assurance.dao.impl;

import java.util.List;

import org.hibernate.Session;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class PaiementDAOImpl extends GenericDAOImpl<Paiement, Integer> implements PaiementDAO {

    public PaiementDAOImpl() {
        
        super(Paiement.class);
    }

    @Override
    public List<Paiement> findByContrat(Integer contratId) {
    
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        
            return session.createQuery(
                "FROM Paiement p WHERE p.contrat.idContrat = :cid ORDER BY p.datePaiement DESC", 
                Paiement.class)
                .setParameter("cid", contratId)
                .getResultList();
        }
    }

    @Override
    public List<Paiement> findEnAttente() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Paiement p WHERE p.statut = 'EN_ATTENTE' ORDER BY p.datePaiement ASC", 
                Paiement.class)
                .getResultList();
        }
    }
}