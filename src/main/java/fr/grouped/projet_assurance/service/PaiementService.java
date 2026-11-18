package fr.grouped.projet_assurance.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class PaiementService {
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    
    public void genererEcheances(Integer contratId, BigDecimal primeTotale, int nbMois) {
        BigDecimal montantMensuel = primeTotale.divide(BigDecimal.valueOf(nbMois), 2, java.math.RoundingMode.HALF_UP);
        
        for (int i = 1; i <= nbMois; i++) {
            Paiement p = new Paiement();
            p.setMontant(montantMensuel);
            p.setStatut("EN_ATTENTE");
            p.setModePaiement("VIREMENT"); 
            
            p.setDatePaiement(LocalDate.now().plusMonths(i)); 
            
           
            fr.grouped.projet_assurance.model.Contrat c = new fr.grouped.projet_assurance.model.Contrat();
            c.setIdContrat(contratId);
            p.setContrat(c);
            
            paiementDAO.save(p);
        }
    }

    
    public void encaisserPaiement(Integer paiementId, String modePaiement) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Paiement p = session.get(Paiement.class, paiementId);
            if (p == null) throw new Exception("Paiement introuvable");
            
            if ("EFFECTUE".equals(p.getStatut())) {
                throw new Exception("Ce paiement a déjà été enregistré.");
            }
            
            p.setStatut("EFFECTUE");
            p.setModePaiement(modePaiement);
            p.setDatePaiement(LocalDate.now()); 
            
            session.merge(p);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}