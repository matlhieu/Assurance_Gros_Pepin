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

    /**
     * Génère l'échéancier d'un contrat : la prime annuelle est répartie en nbEcheances paiements EN_ATTENTE
     * (12 = mensuel, 4 = trimestriel, 1 = annuel), à partir de la date "debut".
     * Ne fait rien si le contrat a déjà des échéances en attente (évite les doublons à la réactivation).
     * @return le nombre d'échéances créées
     */
    public int genererEcheancier(fr.grouped.projet_assurance.model.Contrat contrat, int nbEcheances, LocalDate debut) {
        if (contrat == null || contrat.getIdContrat() == null || contrat.getPrime() == null) return 0;
        if (nbEcheances != 1 && nbEcheances != 4 && nbEcheances != 12) nbEcheances = 12;

        boolean dejaEnAttente = paiementDAO.findByContrat(contrat.getIdContrat()).stream()
                .anyMatch(p -> "EN_ATTENTE".equals(p.getStatut()));
        if (dejaEnAttente) return 0;

        BigDecimal montant = contrat.getPrime().divide(BigDecimal.valueOf(nbEcheances), 2, java.math.RoundingMode.HALF_UP);
        // La dernière échéance absorbe l'arrondi pour que le total soit exactement la prime
        BigDecimal derniere = contrat.getPrime().subtract(montant.multiply(BigDecimal.valueOf(nbEcheances - 1)));
        int pasEnMois = 12 / nbEcheances;

        for (int i = 0; i < nbEcheances; i++) {
            Paiement p = new Paiement();
            p.setContrat(contrat);
            p.setMontant(i == nbEcheances - 1 ? derniere : montant);
            p.setStatut("EN_ATTENTE");
            p.setModePaiement("PRELEVEMENT");
            p.setDatePaiement(debut.plusMonths((long) i * pasEnMois));
            paiementDAO.save(p);
        }
        return nbEcheances;
    }
}
