package fr.grouped.projet_assurance;

import java.util.List;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.util.HibernateUtil;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("--- Démarrage du test de connexion ---");

        try {
            // 1. Test de connexion basique (Hibernate lit le hibernate.cfg.xml)
            System.out.println("1. Initialisation d'Hibernate...");
            HibernateUtil.getSessionFactory().openSession().close();
            System.out.println("✅ Connexion à MySQL réussie !");

            // 2. Test de lecture avec ton DAO
            System.out.println("2. Test de lecture des sinistres...");
            SinistreDAO dao = new SinistreDAOImpl();
            
            // On cherche les sinistres du client Robb Stark (l'ID 3 dans ton data.sql)
            List<Sinistre> sinistres = dao.findByClient(3); 
            
            System.out.println("✅ Le DAO fonctionne. Nombre de sinistres trouvés pour le client 3 : " + sinistres.size());
            
            for (Sinistre s : sinistres) {
                System.out.println("   -> Sinistre : " + s.getNumeroSinistre() + " | Statut : " + s.getStatut());
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                HibernateUtil.shutdown();
            } catch (Exception e) {
                
            }
            System.out.println("--- Fin du test ---");
        }
    }
}