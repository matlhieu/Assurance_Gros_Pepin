package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.model.TypeAssurance;

public class TypeAssuranceDAOImpl
        extends GenericDAOImpl<TypeAssurance, Long>
        implements TypeAssuranceDAO {

    public TypeAssuranceDAOImpl() {
        super(TypeAssurance.class);
    }
}