package fr.grouped.projet_assurance.util;

import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Au démarrage de l'application puis toutes les 6 heures : les contrats ACTIF dont la date de fin
 * est passée deviennent EXPIRE. Personne n'a besoin d'y penser.
 */
@WebListener
public class ExpirationContratsListener implements ServletContextListener {

    private ScheduledExecutorService planificateur;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        planificateur = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "expiration-contrats");
            t.setDaemon(true);
            return t;
        });
        planificateur.scheduleAtFixedRate(() -> {
            try {
                int n = new ContratDAOImpl().expirerContratsEchus();
                if (n > 0) sce.getServletContext().log(n + " contrat(s) passé(s) en EXPIRE");
            } catch (RuntimeException e) {
                // base indisponible au démarrage par exemple : on réessaiera au prochain passage
                sce.getServletContext().log("Expiration des contrats impossible : " + e.getMessage());
            }
        }, 0, 6, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (planificateur != null) planificateur.shutdownNow();
    }
}
