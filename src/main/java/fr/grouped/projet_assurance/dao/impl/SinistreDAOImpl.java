package fr.grouped.projet_assurance.dao.impl;

import java.util.List;

import org.hibernate.Session;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class SinistreDAOImpl extends GenericDAOImpl<Sinistre, Integer> implements SinistreDAO {

    public SinistreDAOImpl() { super(Sinistre.class); }

    @Override
    public List<Sinistre> findByClient(Integer clientId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                "FROM Sinistre si WHERE si.contrat.utilisateur.idUtilisateur = :cid ORDER BY si.dateDeclaration DESC", Sinistre.class)
                .setParameter("cid", clientId)
                .getResultList();
        }
    }

    @Override
    public List<Sinistre> findByStatut(String statut) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                "FROM Sinistre si WHERE si.statut = :st ORDER BY si.dateDeclaration ASC", Sinistre.class)
                .setParameter("st", statut)
                .getResultList();
        }
    }
}