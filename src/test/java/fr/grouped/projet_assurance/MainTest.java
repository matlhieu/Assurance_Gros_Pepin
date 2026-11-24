package fr.grouped.projet_assurance;

import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.model.TypeAssurance;
import fr.grouped.projet_assurance.model.Garantie;

import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        TypeAssuranceDAO typeDAO = new TypeAssuranceDAOImpl();
        GarantieDAO garantieDAO = new GarantieDAOImpl();

        List<TypeAssurance> types = typeDAO.findAll();
        System.out.println("Types d'assurance : " + types.size());
        for (TypeAssurance t : types) {
            System.out.println(" - " + t.getNom());
        }

        List<Garantie> garanties = garantieDAO.findAll();
        System.out.println("Garanties : " + garanties.size());
        for (Garantie g : garanties) {
            System.out.println(" - " + g.getNom());
        }
    }
}