package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.GarantieDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.GarantieDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Garantie;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Garanties du client : celles de ses contrats actifs, et celles qu'il n'a pas encore. */
@WebServlet("/client/garanties")
public class GarantieClientServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final GarantieDAO garantieDAO = new GarantieDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);

        List<Contrat> contratsActifs = contratDAO.findByClientAvecDetails(u.getIdUtilisateur()).stream()
                .filter(c -> c.getStatut() == StatutContrat.ACTIF)
                .toList();

        // Garanties couvertes par au moins un contrat actif (sans doublon)
        Set<Integer> idsCouverts = contratsActifs.stream()
                .flatMap(c -> c.getGaranties().stream())
                .map(Garantie::getIdGarantie)
                .collect(Collectors.toSet());

        List<Garantie> nonCouvertes = garantieDAO.findAll().stream()
                .filter(g -> !idsCouverts.contains(g.getIdGarantie()))
                .toList();

        req.setAttribute("contratsActifs", contratsActifs);
        req.setAttribute("nbGarantiesCouvertes", idsCouverts.size());
        req.setAttribute("nonCouvertes", nonCouvertes);
        req.getRequestDispatcher("/WEB-INF/views/client/garanties.jsp").forward(req, resp);
    }
}
