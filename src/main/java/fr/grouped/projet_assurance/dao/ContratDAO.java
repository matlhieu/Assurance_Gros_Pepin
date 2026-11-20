package fr.grouped.projet_assurance.dao;

import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import java.util.List;

public interface ContratDAO extends GenericDAO<Contrat, Integer> {
    List<Contrat> findByClient(Integer idClient);
    List<Contrat> findByStatut(StatutContrat statut);
    List<Contrat> search(String keyword);
}