package fr.grouped.projet_assurance.dao.impl;

import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.model.Garantie;
import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class GarantieDAOImpl
        extends GenericDAOImpl<Garantie, Integer>
        implements GarantieDAO {

    public GarantieDAOImpl() {
        super(Garantie.class);
    }

}