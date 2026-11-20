package fr.grouped.projet_assurance.dao;

import java.util.List;

import fr.grouped.projet_assurance.model.Paiement;

public interface PaiementDAO extends GenericDAO<Paiement, Integer> {
    
    List<Paiement> findByContrat(Integer contratId);
    
   
    List<Paiement> findEnAttente();
}