package fr.grouped.projet_assurance.dao;
import fr.grouped.projet_assurance.model.Utilisateur;
import java.util.List;


public interface UtilisateurDAO extends GenericDAO<Utilisateur, Integer> {
    Utilisateur findByEmail(String email);
    List<Utilisateur> findByRole(String role);
    boolean emailExists(String email);
}
