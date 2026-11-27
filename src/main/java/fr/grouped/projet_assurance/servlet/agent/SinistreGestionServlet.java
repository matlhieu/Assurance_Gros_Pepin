package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.service.SinistreService;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Traitement des sinistres par un agent (ou un admin) :
 *  GET  /agent/sinistres[?statut=..]   -> liste filtrable
 *  GET  /agent/sinistres?id=..         -> détail + actions
 *  POST /agent/sinistres  action=statut      (id, nouveauStatut, commentaire)
 *  POST /agent/sinistres  action=indemniser  (id, montant)
 * Même servlet sur /admin/sinistres (le filtre gère les rôles).
 */
@WebServlet({"/agent/sinistres", "/admin/sinistres"})
public class SinistreGestionServlet extends HttpServlet {

    public static final List<String> STATUTS = List.of("DECLARE", "EN_ANALYSE", "ACCEPTE", "REFUSE", "INDEMNISE");

    /** Transitions autorisées (identiques aux règles de SinistreService). */
    private static final Map<String, List<String>> TRANSITIONS = Map.of(
            "DECLARE", List.of("EN_ANALYSE"),
            "EN_ANALYSE", List.of("ACCEPTE", "REFUSE"));

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final SinistreService sinistreService = new SinistreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.consommerFlash(req);
        req.setAttribute("baseUrl", req.getContextPath() + req.getServletPath());

        Integer id = WebUtil.parseId(req.getParameter("id"));
        if (id != null) {
            Sinistre s = sinistreDAO.findByIdAvecDetails(id);
            if (s == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("sinistre", s);
            req.setAttribute("transitionsPossibles", TRANSITIONS.getOrDefault(s.getStatut(), List.of()));
            req.getRequestDispatcher("/WEB-INF/views/gestion/sinistres/detail.jsp").forward(req, resp);
            return;
        }

        List<Sinistre> sinistres = sinistreDAO.findAllAvecDetails();

        // Nombre de dossiers par statut (affiché sur les onglets de filtre)
        Map<String, Long> compteurs = new java.util.LinkedHashMap<>();
        for (String st : STATUTS) {
            compteurs.put(st, sinistres.stream().filter(x -> st.equals(x.getStatut())).count());
        }
        req.setAttribute("compteurs", compteurs);
        req.setAttribute("nbTotal", sinistres.size());

        String filtre = req.getParameter("statut");
        if (filtre != null && STATUTS.contains(filtre)) {
            sinistres = sinistres.stream().filter(s -> filtre.equals(s.getStatut())).toList();
            req.setAttribute("filtreStatut", filtre);
        }
        String recherche = req.getParameter("q");
        if (recherche != null && !recherche.isBlank()) {
            String q = recherche.trim().toLowerCase(Locale.ROOT);
            sinistres = sinistres.stream().filter(s ->
                    s.getNumeroSinistre().toLowerCase(Locale.ROOT).contains(q)
                 || s.getContrat().getNumContrat().toLowerCase(Locale.ROOT).contains(q)
                 || s.getContrat().getUtilisateur().getNom().toLowerCase(Locale.ROOT).contains(q)
                 || s.getContrat().getUtilisateur().getPrenom().toLowerCase(Locale.ROOT).contains(q))
                .toList();
        }
        req.setAttribute("sinistres", sinistres);
        req.setAttribute("statuts", STATUTS);
        req.getRequestDispatcher("/WEB-INF/views/gestion/sinistres/liste.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String base = req.getContextPath() + req.getServletPath();
        Integer id = WebUtil.parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendRedirect(base);
            return;
        }

        String action = req.getParameter("action");
        String statutBouton = null;
        if (action != null && action.startsWith("statut:")) {
            // un seul formulaire, plusieurs boutons : value="statut:ACCEPTE", "statut:REFUSE"...
            statutBouton = action.substring("statut:".length());
            action = "statut";
        }
        try {
            if ("statut".equals(action)) {
                String nouveauStatut = statutBouton != null ? statutBouton : req.getParameter("nouveauStatut");
                String commentaire = req.getParameter("commentaire");
                if (nouveauStatut == null || !STATUTS.contains(nouveauStatut)) {
                    throw new IllegalArgumentException("Statut invalide.");
                }
                if (commentaire != null && commentaire.length() > 2000) {
                    throw new IllegalArgumentException("Commentaire trop long (2000 caractères maximum).");
                }
                if ("REFUSE".equals(nouveauStatut) && (commentaire == null || commentaire.isBlank())) {
                    throw new IllegalArgumentException("Un refus doit être justifié par un commentaire.");
                }
                sinistreService.changerStatut(id, nouveauStatut, commentaire);
                WebUtil.flash(req, "succes", "Dossier mis à jour : " + WebUtil.libelle(nouveauStatut).toLowerCase() + ".");
            } else if ("commenter".equals(action)) {
                // Commentaire seul, sans changer le statut (§2.5 : "ajouter des commentaires")
                String commentaire = req.getParameter("commentaire");
                if (commentaire == null || commentaire.isBlank()) {
                    throw new IllegalArgumentException("Le commentaire est vide.");
                }
                if (commentaire.length() > 2000) {
                    throw new IllegalArgumentException("Commentaire trop long (2000 caractères maximum).");
                }
                Sinistre s = sinistreDAO.findById(id);
                if (s == null) throw new IllegalArgumentException("Sinistre introuvable.");
                if ("REFUSE".equals(s.getStatut()) || "INDEMNISE".equals(s.getStatut())) {
                    throw new IllegalArgumentException("Ce dossier est clos.");
                }
                s.setCommentaireAgent(commentaire.trim());
                sinistreDAO.update(s);
                WebUtil.flash(req, "succes", "Commentaire enregistré : le client le voit dans son espace.");
            } else if ("accepterIndemniser".equals(action)) {
                // Raccourci : accepter puis indemniser en une seule action
                BigDecimal montant = WebUtil.parseMontant(req.getParameter("montant"));
                if (montant == null) {
                    throw new IllegalArgumentException("Montant d'indemnisation invalide.");
                }
                Sinistre s = sinistreDAO.findById(id);
                if (s == null) throw new IllegalArgumentException("Sinistre introuvable.");
                if (montant.compareTo(s.getMontantEstime()) > 0) {
                    throw new IllegalArgumentException("Le montant ne peut pas dépasser l'estimation ("
                            + WebUtil.euros(s.getMontantEstime()) + ").");
                }
                sinistreService.changerStatut(id, "ACCEPTE", req.getParameter("commentaire"));
                sinistreService.indemniserSinistre(id, montant);
                WebUtil.flash(req, "succes", "Sinistre accepté et indemnisé de " + WebUtil.euros(montant) + ". Le dossier est clos.");
            } else if ("indemniser".equals(action)) {
                BigDecimal montant = WebUtil.parseMontant(req.getParameter("montant"));
                if (montant == null) {
                    throw new IllegalArgumentException("Montant d'indemnisation invalide.");
                }
                String commentaire = req.getParameter("commentaire");
                if (commentaire != null && !commentaire.isBlank() && commentaire.length() <= 2000) {
                    Sinistre s = sinistreDAO.findById(id);
                    if (s != null) {
                        s.setCommentaireAgent(commentaire.trim());
                        sinistreDAO.update(s);
                    }
                }
                sinistreService.indemniserSinistre(id, montant);
                WebUtil.flash(req, "succes", "Indemnisation de " + WebUtil.euros(montant) + " enregistrée. Le dossier est clos.");
            } else {
                throw new IllegalArgumentException("Action inconnue.");
            }
        } catch (Exception e) {
            // Les messages du service sont écrits pour l'utilisateur (règles métier)
            WebUtil.flash(req, "erreur", e.getMessage() != null ? e.getMessage() : "Opération impossible.");
        }
        resp.sendRedirect(base + "?id=" + id);
    }
}
