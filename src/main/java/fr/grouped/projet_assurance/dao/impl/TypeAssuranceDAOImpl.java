package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.model.TypeAssurance;

public class TypeAssuranceDAOImpl
        extends GenericDAOImpl<TypeAssurance, Integer>
        implements TypeAssuranceDAO {

    public TypeAssuranceDAOImpl() {
        super(TypeAssurance.class);
    }

    @Override
    public java.util.List<TypeAssurance> findAllAvecGaranties() {
        try (org.hibernate.Session session = fr.grouped.projet_assurance.util.HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT t FROM TypeAssurance t LEFT JOIN FETCH t.garantiesDisponibles ORDER BY t.nom",
                    TypeAssurance.class)
                .list();
        }
    }
}
