package fr.grouped.projet_assurance.dao.impl;

import java.util.List;

import org.hibernate.Session;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.model.PieceJointe;
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

    // ---------- Requêtes avec JOIN FETCH (pour l'affichage dans les JSP) ----------

    private static final String SELECT_DETAILS =
        "SELECT DISTINCT si FROM Sinistre si "
      + "JOIN FETCH si.contrat c "
      + "JOIN FETCH c.utilisateur "
      + "JOIN FETCH c.typeAssurance "
      + "LEFT JOIN FETCH si.piecesJointes ";

    @Override
    public List<Sinistre> findAllAvecDetails() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(SELECT_DETAILS + "ORDER BY si.dateDeclaration DESC, si.idSinistre DESC", Sinistre.class)
                .getResultList();
        }
    }

    @Override
    public List<Sinistre> findByClientAvecDetails(Integer clientId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    SELECT_DETAILS + "WHERE c.utilisateur.idUtilisateur = :cid "
                                   + "ORDER BY si.dateDeclaration DESC, si.idSinistre DESC", Sinistre.class)
                .setParameter("cid", clientId)
                .getResultList();
        }
    }

    @Override
    public Sinistre findByIdAvecDetails(Integer idSinistre) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(SELECT_DETAILS + "WHERE si.idSinistre = :id", Sinistre.class)
                .setParameter("id", idSinistre)
                .uniqueResult();
        }
    }

    @Override
    public PieceJointe findPieceJointeAvecProprietaire(Integer idPiece) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT p FROM PieceJointe p "
                  + "JOIN FETCH p.sinistre si JOIN FETCH si.contrat c JOIN FETCH c.utilisateur "
                  + "WHERE p.idPiece = :id", PieceJointe.class)
                .setParameter("id", idPiece)
                .uniqueResult();
        }
    }

    @Override
    public void ajouterPiecesJointes(List<PieceJointe> pieces) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = s.beginTransaction();
            try {
                for (PieceJointe p : pieces) s.persist(p);
                tx.commit();
            } catch (RuntimeException e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
