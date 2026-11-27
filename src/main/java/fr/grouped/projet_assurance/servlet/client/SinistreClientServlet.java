package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.PieceJointe;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import fr.grouped.projet_assurance.service.SinistreService;
import fr.grouped.projet_assurance.util.FichierUtil;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Sinistres côté client :
 *  GET  /client/sinistres            -> liste
 *  GET  /client/sinistres?id=..      -> détail
 *  GET  /client/sinistres/declarer   -> formulaire (option ?contrat=..)
 *  POST /client/sinistres/declarer   -> enregistrement (multipart, avec pièces jointes)
 *  POST /client/sinistres/pieces     -> ajout de justificatifs à un sinistre en cours (multipart)
 */
@WebServlet({"/client/sinistres", "/client/sinistres/declarer", "/client/sinistres/pieces"})
@MultipartConfig(maxFileSize = FichierUtil.TAILLE_MAX, maxRequestSize = 6 * FichierUtil.TAILLE_MAX)
public class SinistreClientServlet extends HttpServlet {

    public static final List<String> TYPES_SINISTRE = List.of(
            "Accident", "Vol", "Incendie", "Dégât des eaux", "Bris de glace",
            "Catastrophe naturelle", "Santé", "Autre");
    private static final int MAX_FICHIERS = 5;

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final SinistreService sinistreService = new SinistreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        WebUtil.consommerFlash(req);

        if (req.getServletPath().endsWith("/declarer")) {
            afficherFormulaire(req, resp, u);
            return;
        }

        Integer id = WebUtil.parseId(req.getParameter("id"));
        if (id == null) {
            req.setAttribute("sinistres", sinistreDAO.findByClientAvecDetails(u.getIdUtilisateur()));
            req.getRequestDispatcher("/WEB-INF/views/client/sinistres/liste.jsp").forward(req, resp);
            return;
        }

        Sinistre s = sinistreDAO.findByIdAvecDetails(id);
        if (s == null || s.getContrat().getUtilisateur().getIdUtilisateur() != u.getIdUtilisateur()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("sinistre", s);
        req.getRequestDispatcher("/WEB-INF/views/client/sinistres/detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (req.getServletPath().endsWith("/pieces")) {
            ajouterPieces(req, resp);
            return;
        }
        if (!req.getServletPath().endsWith("/declarer")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Utilisateur u = WebUtil.utilisateurConnecte(req);

        String idContratStr = req.getParameter("idContrat");
        String typeSinistre = req.getParameter("typeSinistre");
        String description = req.getParameter("description");
        String montantStr = req.getParameter("montantEstime");

        Map<String, String> erreurs = new LinkedHashMap<>();

        // --- Contrat : doit exister, appartenir au client et être actif ---
        Contrat contrat = null;
        Integer idContrat = WebUtil.parseId(idContratStr);
        if (idContrat != null) {
            contrat = contratDAO.findByIdAvecDetails(idContrat);
        }
        if (contrat == null || contrat.getUtilisateur().getIdUtilisateur() != u.getIdUtilisateur()) {
            erreurs.put("idContrat", "Veuillez choisir un de vos contrats.");
            contrat = null;
        } else if (contrat.getStatut() != StatutContrat.ACTIF) {
            erreurs.put("idContrat", "Ce contrat n'est pas actif : impossible d'y déclarer un sinistre.");
        }

        if (typeSinistre == null || !TYPES_SINISTRE.contains(typeSinistre)) {
            erreurs.put("typeSinistre", "Veuillez choisir un type de sinistre.");
        }
        if (description == null || description.trim().length() < 10) {
            erreurs.put("description", "Décrivez le sinistre (10 caractères minimum).");
        } else if (description.length() > 2000) {
            erreurs.put("description", "Description trop longue (2000 caractères maximum).");
        }
        BigDecimal montant = WebUtil.parseMontant(montantStr);
        if (montant == null) {
            erreurs.put("montantEstime", "Indiquez un montant estimé supérieur à 0.");
        } else if (montant.compareTo(new BigDecimal("1000000")) > 0) {
            erreurs.put("montantEstime", "Montant trop élevé.");
        }

        // --- Pièces jointes ---
        List<Part> fichiers = new ArrayList<>();
        boolean tropVolumineux = false;
        try {
            for (Part p : req.getParts()) {
                if ("piecesJointes".equals(p.getName()) && p.getSize() > 0
                        && p.getSubmittedFileName() != null && !p.getSubmittedFileName().isBlank()) {
                    fichiers.add(p);
                }
            }
        } catch (IllegalStateException e) {
            // levée par le conteneur quand maxFileSize / maxRequestSize est dépassé
            tropVolumineux = true;
        }
        if (tropVolumineux) {
            erreurs.put("piecesJointes", "Fichiers trop volumineux : 5 Mo maximum par fichier.");
        } else if (fichiers.isEmpty()) {
            erreurs.put("piecesJointes", "Au moins une pièce justificative est obligatoire (photo, facture, constat…).");
        } else if (fichiers.size() > MAX_FICHIERS) {
            erreurs.put("piecesJointes", "5 fichiers maximum.");
        } else {
            for (Part p : fichiers) {
                String err = FichierUtil.verifier(p);
                if (err != null) {
                    erreurs.put("piecesJointes", err);
                    break;
                }
            }
        }

        if (!erreurs.isEmpty()) {
            req.setAttribute("erreurs", erreurs);
            afficherFormulaire(req, resp, u);
            return;
        }

        // --- Enregistrement ---
        Sinistre s = new Sinistre();
        s.setNumeroSinistre("SIN-" + Year.now() + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT));
        s.setContrat(contrat);
        s.setTypeSinistre(typeSinistre);
        s.setDescription(description.trim());
        s.setMontantEstime(montant);

        List<String> fichiersEcrits = new ArrayList<>();
        try {
            for (Part p : fichiers) {
                String nomStocke = FichierUtil.enregistrer(p);
                fichiersEcrits.add(nomStocke);
                PieceJointe pj = new PieceJointe();
                pj.setNomFichier(FichierUtil.nomAffichable(p));
                pj.setCheminFichier(nomStocke);
                pj.setSinistre(s);
                s.getPiecesJointes().add(pj);
            }
            sinistreService.declarerSinistre(s);
        } catch (Exception e) {
            // En cas d'échec, on ne laisse pas de fichiers orphelins sur le disque
            fichiersEcrits.forEach(FichierUtil::supprimerSilencieusement);
            log("Échec de la déclaration de sinistre", e);
            erreurs.put("global", "La déclaration n'a pas pu être enregistrée. Réessayez plus tard.");
            req.setAttribute("erreurs", erreurs);
            afficherFormulaire(req, resp, u);
            return;
        }

        WebUtil.flash(req, "succes", "Votre sinistre " + s.getNumeroSinistre()
                + " a bien été déclaré. Un agent va l'examiner.");
        resp.sendRedirect(req.getContextPath() + "/client/sinistres?id=" + s.getIdSinistre());
    }

    /** Ajout de justificatifs à un sinistre du client, tant qu'il n'est pas clos. */
    private void ajouterPieces(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        Integer id = WebUtil.parseId(req.getParameter("id"));
        Sinistre s = (id == null) ? null : sinistreDAO.findByIdAvecDetails(id);
        if (s == null || s.getContrat().getUtilisateur().getIdUtilisateur() != u.getIdUtilisateur()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String retour = req.getContextPath() + "/client/sinistres?id=" + id;
        if ("REFUSE".equals(s.getStatut()) || "INDEMNISE".equals(s.getStatut())) {
            WebUtil.flash(req, "erreur", "Ce dossier est clos : il n'est plus possible d'ajouter de justificatif.");
            resp.sendRedirect(retour);
            return;
        }

        List<Part> fichiers = new ArrayList<>();
        try {
            for (Part p : req.getParts()) {
                if ("piecesJointes".equals(p.getName()) && p.getSize() > 0
                        && p.getSubmittedFileName() != null && !p.getSubmittedFileName().isBlank()) {
                    fichiers.add(p);
                }
            }
        } catch (IllegalStateException e) {
            WebUtil.flash(req, "erreur", "Fichiers trop volumineux : 5 Mo maximum par fichier.");
            resp.sendRedirect(retour);
            return;
        }
        String erreur = null;
        if (fichiers.isEmpty()) erreur = "Choisissez au moins un fichier.";
        else if (s.getPiecesJointes().size() + fichiers.size() > 10) erreur = "10 justificatifs maximum par sinistre.";
        else {
            for (Part p : fichiers) {
                erreur = FichierUtil.verifier(p);
                if (erreur != null) break;
            }
        }
        if (erreur != null) {
            WebUtil.flash(req, "erreur", erreur);
            resp.sendRedirect(retour);
            return;
        }

        List<String> ecrits = new ArrayList<>();
        List<PieceJointe> pieces = new ArrayList<>();
        try {
            for (Part p : fichiers) {
                String nomStocke = FichierUtil.enregistrer(p);
                ecrits.add(nomStocke);
                PieceJointe pj = new PieceJointe();
                pj.setNomFichier(FichierUtil.nomAffichable(p));
                pj.setCheminFichier(nomStocke);
                pj.setSinistre(s);
                pieces.add(pj);
            }
            sinistreDAO.ajouterPiecesJointes(pieces);
        } catch (Exception e) {
            ecrits.forEach(FichierUtil::supprimerSilencieusement);
            log("Échec de l'ajout de pièces jointes", e);
            WebUtil.flash(req, "erreur", "Les fichiers n'ont pas pu être enregistrés. Réessayez plus tard.");
            resp.sendRedirect(retour);
            return;
        }
        WebUtil.flash(req, "succes", pieces.size() + " justificatif(s) ajouté(s). L'agent en charge du dossier les verra tout de suite.");
        resp.sendRedirect(retour);
    }

    private void afficherFormulaire(HttpServletRequest req, HttpServletResponse resp, Utilisateur u)
            throws ServletException, IOException {
        List<Contrat> contratsActifs = contratDAO.findByClientAvecDetails(u.getIdUtilisateur()).stream()
                .filter(c -> c.getStatut() == StatutContrat.ACTIF)
                .toList();
        req.setAttribute("contratsActifs", contratsActifs);
        req.setAttribute("typesSinistre", TYPES_SINISTRE);
        req.getRequestDispatcher("/WEB-INF/views/client/sinistres/declarer.jsp").forward(req, resp);
    }
}
