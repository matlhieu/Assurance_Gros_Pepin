package fr.grouped.projet_assurance.dao;

import java.util.List;

import fr.grouped.projet_assurance.model.Sinistre;

public interface SinistreDAO extends GenericDAO<Sinistre, Integer> {
    List<Sinistre> findByClient(Integer clientId);
    List<Sinistre> findByStatut(String statut);
}