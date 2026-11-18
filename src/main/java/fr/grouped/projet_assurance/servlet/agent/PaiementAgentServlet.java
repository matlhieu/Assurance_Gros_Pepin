package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.impl.PaiementDao;
import fr.grouped.projet_assurance.model.Paiement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PaiementAgentServlet", urlPatterns = {"/agent/paiements", "/agents/paiements"})
public class PaiementAgentServlet extends HttpServlet {

    private PaiementDao paiementDao = new PaiementDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Paiement> tousLesPaiements;

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

        HttpSession session = request.getSession();
        String messageSucces = (String) session.getAttribute("messageSucces");
        if (messageSucces != null) {
            request.setAttribute("messageSucces", messageSucces);
            session.removeAttribute("messageSucces");
        }

        request.setAttribute("paiements", tousLesPaiements);
        request.setAttribute("paiementsEffectues", paiementsEffectues);
        request.setAttribute("paiementsEnAttente", paiementsEnAttente);
        request.setAttribute("totalPaye", totalPaye);
        request.setAttribute("totalEnAttente", totalEnAttente);
        request.setAttribute("isAgent", true);

        request.getRequestDispatcher("/WEB-INF/paiements.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // validation directe d'une échéance existante (action encaisser)
        if ("encaisser".equalsIgnoreCase(action)) {
            String idPaiementStr = request.getParameter("idPaiement");
            if (idPaiementStr != null && !idPaiementStr.trim().isEmpty()) {
                try {
                    int idPaiement = Integer.parseInt(idPaiementStr.trim());
                    Paiement paiement = paiementDao.findById(idPaiement);
                    if (paiement != null) {
                        paiement.setStatut("EFFECTUE");
                        paiement.setDatePaiement(LocalDate.now());

                        String mode = request.getParameter("modePaiement");
                        if (mode != null && !mode.trim().isEmpty()) {
                            paiement.setModePaiement(mode);
                        }

                        paiementDao.update(paiement);
                        request.getSession().setAttribute("messageSucces", "Échéance N°" + idPaiement + " validée comme payée avec succès !");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("messageErreur", "Identifiant de paiement invalide.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/agent/paiements");
            return;
        }

        //enregistrement d'un nouvel encaissement
        String idContratStr = request.getParameter("idContrat");
        String montantStr = request.getParameter("montant");
        String datePaiementStr = request.getParameter("datePaiement");
        String modePaiement = request.getParameter("modePaiement");
        String statut = request.getParameter("statut");

        String erreur = null;
        int idContrat = 0;
        BigDecimal montant = null;
        LocalDate datePaiement = null;

        if (idContratStr == null || idContratStr.trim().isEmpty()) {
            erreur = "Veuillez renseigner le numéro du contrat.";
        } else {
            try {
                idContrat = Integer.parseInt(idContratStr.trim());
                if (idContrat <= 0) {
                    erreur = "Le numéro de contrat doit être un entier positif.";
                }
            } catch (NumberFormatException e) {
                erreur = "Le numéro de contrat est invalide (entier attendu).";
            }
        }

        if (erreur == null) {
            if (montantStr == null || montantStr.trim().isEmpty()) {
                erreur = "Veuillez renseigner le montant à encaisser.";
            } else {
                try {
                    montant = new BigDecimal(montantStr.trim().replace(',', '.'));
                    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                        erreur = "Le montant doit être supérieur à 0.";
                    }
                } catch (NumberFormatException e) {
                    erreur = "Le montant saisi est invalide (ex: 150.50).";
                }
            }
        }

        if (erreur == null) {
            if (modePaiement == null || modePaiement.trim().isEmpty()) {
                erreur = "Veuillez sélectionner un mode de paiement.";
            }
        }

        if (erreur == null) {
            if (datePaiementStr != null && !datePaiementStr.trim().isEmpty()) {
                try {
                    datePaiement = LocalDate.parse(datePaiementStr.trim());
                } catch (Exception e) {
                    erreur = "La date saisie est invalide (format attendu : AAAA-MM-JJ).";
                }
            } else {
                datePaiement = LocalDate.now();
            }
        }

        if (statut == null || statut.trim().isEmpty()) {
            statut = "EFFECTUE";
        }

        if (erreur != null) {
            request.setAttribute("messageErreur", erreur);
            request.setAttribute("saisieIdContrat", idContratStr);
            request.setAttribute("saisieMontant", montantStr);
            request.setAttribute("saisieDatePaiement", datePaiementStr);
            request.setAttribute("saisieModePaiement", modePaiement);
            request.setAttribute("saisieStatut", statut);
            doGet(request, response);
        } else {
            Paiement nouveauPaiement = new Paiement(montant, datePaiement, modePaiement, statut, idContrat);
            paiementDao.save(nouveauPaiement);

            request.getSession().setAttribute("messageSucces", "Encaissement de " + montant + " € enregistré avec succès pour le contrat N°" + idContrat + " !");
            response.sendRedirect(request.getContextPath() + "/agent/paiements");
        }
    }
}
