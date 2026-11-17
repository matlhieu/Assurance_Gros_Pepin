package fr.grouped.projet_assurance.dao.impl;


import fr.grouped.projet_assurance.dao.UtilisateurDAO;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class UtilisateurDAOImpl
        extends GenericDAOImpl<Utilisateur, Integer>
        implements UtilisateurDAO {

    public UtilisateurDAOImpl() { super(Utilisateur.class); }

    @Override
    public Utilisateur findByEmail(String email) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Utilisateur u where u.email = :e", Utilisateur.class)
                    .setParameter("e", email)
                    .uniqueResult();
        }
    }

    @Override
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public List<Utilisateur> findByRole(String role) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Utilisateur u where u.role = :r", Utilisateur.class)
                    .setParameter("r", role)
                    .list();
        }
    }
}