package fr.grouped.projet_assurance.servlet.commun;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.TypeAssuranceDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.dao.impl.TypeAssuranceDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.model.TypeAssurance;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.service.IaService;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assistant IA, ouvert à tout le monde (visiteurs, clients, agents, admins).
 *
 *  GET  /assistant            -> conversation en cours
 *  POST question=...          -> pose une question (Post/Redirect/Get)
 *  POST action=effacer        -> nouvelle conversation
 *
 * Le contexte donné à l'IA dépend de qui parle :
 *  - CLIENT connecté      -> son dossier (contrats, garanties, sinistres, paiements)
 *  - AGENT / ADMIN        -> l'offre et les procédures (pas de dossier personnel)
 *  - Visiteur non connecté-> l'offre et les procédures, invitation à créer un compte
 *
 * La page étant publique, un compteur simple en session limite à 30 questions
 * par heure et par visiteur pour ne pas épuiser le quota de la clé API.
 * La conversation (10 derniers échanges) est gardée en session.
 */
@WebServlet("/assistant")
public class AssistantServlet extends HttpServlet {

    private static final int MAX_ECHANGES = 10;
    private static final int MAX_QUESTION = 500;
    private static final int MAX_QUESTIONS_PAR_HEURE = 30;
    private static final long FENETRE_MS = 3_600_000L; // 1 heure

    /** Un échange question / réponse (getters pour la JSP). */
    public record Echange(String question, String reponse) {
        public String getQuestion() { return question; }
        public String getReponse() { return reponse; }
    }

    private final IaService iaService = new IaService();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final TypeAssuranceDAO typeDAO = new TypeAssuranceDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        req.setAttribute("conversation", conversation(req.getSession()));
        // Les questions suggérées sont définies côté serveur : plus simple à maintenir que des
        // listes écrites dans la JSP (les apostrophes françaises cassent les listes littérales EL).
        req.setAttribute("suggestions", suggestions(u));
        req.getRequestDispatcher("/WEB-INF/views/commun/assistant.jsp").forward(req, resp);
    }

    /** Questions cliquables affichées au-dessus du champ de saisie, selon le profil. */
    private static List<String> suggestions(Utilisateur u) {
        if (u == null) {
            return List.of(
                    "Quelles assurances proposez-vous ?",
                    "Que couvre l'assurance habitation ?",
                    "Combien coûte l'assurance automobile ?",
                    "Comment créer un compte ?",
                    "Comment déclarer un sinistre ?",
                    "Quelle est la différence entre la garantie vol et la responsabilité civile ?");
        }
        if ("CLIENT".equals(u.getRole())) {
            return List.of(
                    "Quels sont mes contrats actifs ?",
                    "Quelles garanties possède mon assurance automobile ?",
                    "Où en est mon dernier sinistre ?",
                    "Comment déclarer un sinistre ?",
                    "Quels documents dois-je fournir pour mon sinistre ?",
                    "Quelle est la différence entre la garantie vol et la responsabilité civile ?");
        }
        return List.of(
                "Quelles assurances proposez-vous ?",
                "Comment souscrire un contrat pour un client ?",
                "Comment déclarer un sinistre ?",
                "Quelles sont les étapes du traitement d'un sinistre ?");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        if ("effacer".equals(req.getParameter("action"))) {
            session.removeAttribute("conversationIa");
            resp.sendRedirect(req.getContextPath() + "/assistant");
            return;
        }

        String question = req.getParameter("question");
        if (question != null && !question.isBlank()) {
            if (!sousQuota(session)) {
                WebUtil.flash(req, "erreur",
                        "Vous avez posé beaucoup de questions ! L'assistant fait une petite pause, réessayez dans quelques minutes.");
                resp.sendRedirect(req.getContextPath() + "/assistant#fin");
                return;
            }

            question = question.trim();
            if (question.length() > MAX_QUESTION) question = question.substring(0, MAX_QUESTION);

            Utilisateur u = WebUtil.utilisateurConnecte(req);
            List<Echange> conv = conversation(session);
            String contexte = contexte(u) + historique(conv);

            String reponse = iaService.askAssistant(question, contexte);
            conv.add(new Echange(question, reponse));
            while (conv.size() > MAX_ECHANGES) conv.remove(0);
        }
        // Post/Redirect/Get : un F5 ne renvoie pas la question
        resp.sendRedirect(req.getContextPath() + "/assistant#fin");
    }

    /** Compteur anti-abus : 30 questions max par heure et par session. Renvoie true si la question est autorisée. */
    private boolean sousQuota(HttpSession session) {
        long maintenant = System.currentTimeMillis();
        Long debut = (Long) session.getAttribute("iaFenetreDebut");
        Integer nb = (Integer) session.getAttribute("iaNbQuestions");
        if (debut == null || nb == null || maintenant - debut > FENETRE_MS) {
            debut = maintenant;
            nb = 0;
        }
        if (nb >= MAX_QUESTIONS_PAR_HEURE) return false;
        nb++;
        session.setAttribute("iaFenetreDebut", debut);
        session.setAttribute("iaNbQuestions", nb);
        return true;
    }

    @SuppressWarnings("unchecked")
    private static List<Echange> conversation(HttpSession session) {
        Object o = session.getAttribute("conversationIa");
        if (o instanceof List<?>) return (List<Echange>) o;
        List<Echange> conv = new ArrayList<>();
        session.setAttribute("conversationIa", conv);
        return conv;
    }

    /** Le contexte dépend du rôle : dossier personnel pour un CLIENT, offre publique sinon. */
    private String contexte(Utilisateur u) {
        if (u == null) return contexteVisiteur();
        if ("CLIENT".equals(u.getRole())) return contexteClient(u);
        return contextePersonnel(u);
    }

    /** Contexte des visiteurs non connectés : l'offre commerciale, sans aucune donnée personnelle. */
    private String contexteVisiteur() {
        StringBuilder sb = new StringBuilder();
        sb.append("La personne qui parle est un VISITEUR non connecté. Ne parle pas de « son dossier » ou de « ses contrats » : ")
          .append("présente l'offre de Gros Pépin et invite-le à créer un compte ou à contacter un agent.\n")
          .append("Date du jour : ").append(WebUtil.date(LocalDate.now())).append(".\n")
          .append("\n").append(offreGrosPepin());
        return sb.toString();
    }

    /** Contexte des AGENT / ADMIN : l'offre et les procédures, mais pas de dossier personnel. */
    private String contextePersonnel(Utilisateur u) {
        StringBuilder sb = new StringBuilder();
        sb.append("La personne qui parle est un membre du personnel (rôle ").append(u.getRole())
          .append("), pas un client : ne cherche pas son dossier personnel, réponds sur l'offre et les procédures.\n")
          .append("Date du jour : ").append(WebUtil.date(LocalDate.now())).append(".\n")
          .append("\n").append(offreGrosPepin());
        return sb.toString();
    }

    /** Offre commerciale publique : types d'assurance, prix de base et garanties associées. */
    private String offreGrosPepin() {
        StringBuilder sb = new StringBuilder("OFFRE GROS PÉPIN :\n");
        try {
            List<TypeAssurance> types = typeDAO.findAllAvecGaranties();
            if (types.isEmpty()) sb.append("- (aucun type d'assurance en base)\n");
            for (TypeAssurance t : types) {
                sb.append("- Assurance ").append(t.getNom());
                if (t.getPrixBase() != null) sb.append(", à partir de ").append(WebUtil.euros(t.getPrixBase())).append(" par an");
                if (t.getDescription() != null && !t.getDescription().isBlank()) sb.append(" : ").append(t.getDescription());
                if (t.getGarantiesDisponibles() != null && !t.getGarantiesDisponibles().isEmpty()) {
                    sb.append(". Garanties possibles : ").append(t.getGarantiesDisponibles().stream()
                            .map(g -> g.getNom() + (g.getDescription() == null || g.getDescription().isBlank() ? "" : " (" + g.getDescription() + ")"))
                            .collect(Collectors.joining(", ")));
                }
                sb.append('\n');
            }
        } catch (RuntimeException e) {
            sb.append("- (offre momentanément indisponible)\n");
        }
        sb.append("""
                
                INFORMATIONS UTILES :
                - Pour souscrire : créer un compte (bouton Inscription), puis demander un contrat depuis « Mon espace » ; un agent valide la demande.
                - Pour déclarer un sinistre : menu « Mes sinistres » puis « Déclarer un sinistre », choisir le contrat (actif), la nature, décrire les faits, \
                indiquer un montant estimé et joindre au moins un justificatif (PDF, JPG ou PNG, 5 Mo max).
                - Étapes d'un sinistre : Déclaré, En analyse, Accepté ou Refusé, puis Indemnisé (dans la limite du montant estimé).
                Réponds uniquement à partir de ces informations ; si une information manque, dis-le et propose de contacter un agent.
                """);
        return sb.toString();
    }

    /** Tout ce que l'assistant doit savoir sur le client pour répondre aux questions sur son dossier. */
    private String contexteClient(Utilisateur u) {
        int id = u.getIdUtilisateur();
        List<Contrat> contrats = contratDAO.findByClientAvecDetails(id);
        List<Sinistre> sinistres = sinistreDAO.findByClientAvecDetails(id);
        List<Paiement> paiements = paiementDAO.findByClientId(id);

        StringBuilder sb = new StringBuilder();
        sb.append("Client : ").append(u.getPrenom()).append(' ').append(u.getNom())
          .append(". Date du jour : ").append(WebUtil.date(LocalDate.now())).append(".\n");

        sb.append("\nCONTRATS :\n");
        if (contrats.isEmpty()) sb.append("- aucun contrat\n");
        for (Contrat c : contrats) {
            sb.append("- Assurance ").append(c.getTypeAssurance().getNom())
              .append(", n° ").append(c.getNumContrat())
              .append(", statut ").append(c.getStatut().getLibelle())
              .append(", du ").append(WebUtil.date(c.getDateDebut())).append(" au ").append(WebUtil.date(c.getDateFin()))
              .append(", prime ").append(WebUtil.euros(c.getPrime())).append(" par an")
              .append(", garanties : ")
              .append(c.getGaranties().isEmpty() ? "aucune" : c.getGaranties().stream()
                      .map(g -> g.getNom() + " (" + (g.getDescription() == null ? "" : g.getDescription()) + ")")
                      .collect(Collectors.joining(", ")))
              .append('\n');
        }

        sb.append("\nSINISTRES (du plus récent au plus ancien) :\n");
        if (sinistres.isEmpty()) sb.append("- aucun sinistre déclaré\n");
        for (Sinistre s : sinistres) {
            sb.append("- n° ").append(s.getNumeroSinistre())
              .append(", ").append(s.getTypeSinistre())
              .append(", déclaré le ").append(WebUtil.date(s.getDateDeclaration()))
              .append(", contrat ").append(s.getContrat().getNumContrat())
              .append(", statut ").append(WebUtil.libelle(s.getStatut()))
              .append(", montant estimé ").append(WebUtil.euros(s.getMontantEstime()));
            if (s.getMontantIndemnise() != null) sb.append(", indemnisé ").append(WebUtil.euros(s.getMontantIndemnise()));
            sb.append(", ").append(s.getPiecesJointes().size()).append(" pièce(s) jointe(s)");
            if (s.getCommentaireAgent() != null && !s.getCommentaireAgent().isBlank()) {
                sb.append(", message de l'agent : « ").append(s.getCommentaireAgent()).append(" »");
            }
            sb.append('\n');
        }

        sb.append("\nPAIEMENTS :\n");
        if (paiements.isEmpty()) sb.append("- aucun paiement\n");
        for (Paiement p : paiements) {
            sb.append("- ").append(WebUtil.euros(p.getMontant()))
              .append(" le ").append(WebUtil.date(p.getDatePaiement()))
              .append(", contrat ").append(p.getContrat().getNumContrat())
              .append(", ").append(WebUtil.libelle(p.getModePaiement()))
              .append(", ").append(WebUtil.libelle(p.getStatut())).append('\n');
        }

        sb.append("""
                
                INFORMATIONS UTILES :
                - Pour déclarer un sinistre : menu « Mes sinistres » puis « Déclarer un sinistre », choisir le contrat (actif), la nature, décrire les faits, \
                indiquer un montant estimé et joindre au moins un justificatif (PDF, JPG ou PNG, 5 Mo max).
                - Documents utiles : photos des dégâts, factures ou devis, constat amiable (accident), dépôt de plainte (vol), rapport des pompiers (incendie), \
                attestation du syndic ou du plombier (dégât des eaux). On peut en ajouter tant que le dossier est en cours.
                - Étapes d'un sinistre : Déclaré, En analyse, Accepté ou Refusé, puis Indemnisé (dans la limite du montant estimé).
                Réponds uniquement à partir de ces informations ; si une information manque, dis-le et propose de contacter un agent.
                """);
        return sb.toString();
    }

    private static String historique(List<Echange> conv) {
        if (conv.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("\nÉCHANGES PRÉCÉDENTS (pour comprendre les questions de suivi) :\n");
        for (Echange e : conv.subList(Math.max(0, conv.size() - 4), conv.size())) {
            sb.append("Client : ").append(e.question()).append("\nAssistant : ").append(e.reponse()).append('\n');
        }
        return sb.toString();
    }
}
