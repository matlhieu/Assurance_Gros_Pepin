package fr.grouped.projet_assurance.dao;

import fr.grouped.projet_assurance.model.TypeAssurance;
import java.util.List;

public interface TypeAssuranceDAO extends GenericDAO<TypeAssurance, Integer> {

    /** Tous les types d'assurance avec leurs garanties disponibles (pour l'affichage dans les JSP). */
    java.util.List<TypeAssurance> findAllAvecGaranties();

}