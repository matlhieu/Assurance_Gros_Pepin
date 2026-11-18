package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.impl.PaiementDao;
import fr.grouped.projet_assurance.model.Paiement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// On mappe /client/paiements et /clients/paiements pour que les deux URL fonctionnent sans erreur 404
@WebServlet(name = "PaiementClientServlet", urlPatterns = {"/client/paiements", "/clients/paiements"})
public class PaiementClientServlet extends HttpServlet {

    private PaiementDao paiementDao = new PaiementDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Paiement> tousLesPaiements;

        // Possibilité de filtrer par idContrat si précisé dans l'URL (?idContrat=1)
        String idContratStr = request.getParameter("idContrat");
        if (idContratStr != null && !idContratStr.trim().isEmpty()) {
            try {
                int idContrat = Integer.parseInt(idContratStr.trim());
                tousLesPaiements = paiementDao.findByContrat(idContrat);
                request.setAttribute("filtreContrat", idContrat);
            } catch (NumberFormatException e) {
                tousLesPaiements = paiementDao.findAll();
            }
        } else {
            tousLesPaiements = paiementDao.findAll();
        }

        // Séparation : paiements déjà réglés (historique) vs échéances en attente
        List<Paiement> paiementsEffectues = new ArrayList<>();
        List<Paiement> paiementsEnAttente = new ArrayList<>();

        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalEnAttente = BigDecimal.ZERO;

        for (Paiement p : tousLesPaiements) {
            if ("EFFECTUE".equalsIgnoreCase(p.getStatut())) {
                paiementsEffectues.add(p);
                totalPaye = totalPaye.add(p.getMontant());
            } else if ("EN_ATTENTE".equalsIgnoreCase(p.getStatut())) {
                paiementsEnAttente.add(p);
                totalEnAttente = totalEnAttente.add(p.getMontant());
            }
        }

        // Transmission des données à la vue JSP
        request.setAttribute("paiements", tousLesPaiements);
        request.setAttribute("paiementsEffectues", paiementsEffectues);
        request.setAttribute("paiementsEnAttente", paiementsEnAttente);
        request.setAttribute("totalPaye", totalPaye);
        request.setAttribute("totalEnAttente", totalEnAttente);
        request.setAttribute("isAgent", false);

        request.getRequestDispatcher("/WEB-INF/paiements.jsp").forward(request, response);
    }
}
