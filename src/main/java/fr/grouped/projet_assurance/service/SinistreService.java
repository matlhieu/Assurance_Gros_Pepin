package fr.grouped.projet_assurance.service;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.Transaction;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class SinistreService {
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    
    public void declarerSinistre(Sinistre s) throws Exception {
        
        if (s.getMontantEstime() == null || s.getMontantEstime().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Le montant estimé doit être supérieur à 0.");
        }
        
        if (s.getPiecesJointes() == null || s.getPiecesJointes().isEmpty()) {
            throw new Exception("Au moins une pièce justificative est obligatoire.");
        }
        
        
        s.setStatut("DECLARE");
        s.setDateDeclaration(java.time.LocalDate.now());
        
        sinistreDAO.save(s);
    }

    
    public void changerStatut(Integer sinistreId, String nouveauStatut, String commentaire) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new Exception("Sinistre introuvable");
            
         
            String statutActuel = s.getStatut();
            
            
            if ("REFUSE".equals(statutActuel) || "INDEMNISE".equals(statutActuel)) {
                throw new Exception("Ce sinistre est clos. Aucune modification n'est possible.");
            }
            
            
            if ("DECLARE".equals(statutActuel) && !"EN_ANALYSE".equals(nouveauStatut)) {
                throw new Exception("Un sinistre DECLARE ne peut passer qu'en EN_ANALYSE.");
            }
            if ("EN_ANALYSE".equals(statutActuel) && !"ACCEPTE".equals(nouveauStatut) && !"REFUSE".equals(nouveauStatut)) {
                throw new Exception("Un sinistre EN_ANALYSE ne peut passer qu'en ACCEPTE ou REFUSE.");
            }
            
            
            s.setStatut(nouveauStatut);
            if (commentaire != null && !commentaire.trim().isEmpty()) {
                s.setCommentaireAgent(commentaire);
            }
            
            session.merge(s);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    
    public void indemniserSinistre(Integer sinistreId, BigDecimal montant) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Sinistre s = session.get(Sinistre.class, sinistreId);
            if (s == null) throw new Exception("Sinistre introuvable");
            
            if (!"ACCEPTE".equals(s.getStatut())) {
                throw new Exception("Seul un sinistre ACCEPTE peut être indemnisé.");
            }
            if (montant == null || montant.compareTo(s.getMontantEstime()) > 0) {
                throw new Exception("Le montant indemnisé ne peut pas dépasser le montant estimé.");
            }
            
            s.setMontantIndemnise(montant);
            s.setStatut("INDEMNISE");
            session.merge(s);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}