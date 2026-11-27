package fr.grouped.projet_assurance.dao;

import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import java.util.List;

public interface ContratDAO extends GenericDAO<Contrat, Integer> {
    List<Contrat> findByClient(Integer idClient);
    List<Contrat> findByStatut(StatutContrat statut);
    List<Contrat> search(String keyword);

    // Versions qui chargent aussi client, type et garanties (utilisables dans les JSP,
    // une fois la session Hibernate fermée, sans LazyInitializationException)
    List<Contrat> findAllAvecDetails();
    List<Contrat> findByClientAvecDetails(Integer idClient);
    Contrat findByIdAvecDetails(Integer idContrat);

    /** Passe en EXPIRE les contrats actifs dont la date de fin est dépassée. @return nombre de contrats modifiés */
    int expirerContratsEchus();
}
