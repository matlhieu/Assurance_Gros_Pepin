package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Garantie;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Sinistre;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assistant IA du client.
 *  GET  /client/assistant              -> conversation en cours
 *  POST question=..                    -> envoie la question avec le contexte du client (contrats, garanties, sinistres, paiements)
 *  POST action=effacer                 -> nouvelle conversation
 * La conversation est gardée en session (10 derniers échanges) pour que l'assistant comprenne les questions de suivi.
 */
@WebServlet("/client/assistant")
public class ChatIaServlet extends HttpServlet {

    private static final int MAX_ECHANGES = 10;
    private static final int MAX_QUESTION = 500;

    /** Un échange question / réponse (getters pour la JSP). */
    public record Echange(String question, String reponse) {
        public String getQuestion() { return question; }
        public String getReponse() { return reponse; }
    }

    private final IaService iaService = new IaService();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("conversation", conversation(req.getSession()));
        req.getRequestDispatcher("/WEB-INF/views/client/assistant.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        if ("effacer".equals(req.getParameter("action"))) {
            session.removeAttribute("conversationIa");
            resp.sendRedirect(req.getContextPath() + "/client/assistant");
            return;
        }

        String question = req.getParameter("question");
        if (question != null && !question.isBlank()) {
            question = question.trim();
            if (question.length() > MAX_QUESTION) question = question.substring(0, MAX_QUESTION);

            Utilisateur u = WebUtil.utilisateurConnecte(req);
            List<Echange> conv = conversation(session);
            String contexte = contexteClient(u) + historique(conv);

            String reponse = iaService.askAssistant(question, contexte);
            conv.add(new Echange(question, reponse));
            while (conv.size() > MAX_ECHANGES) conv.remove(0);
        }
        // Post/Redirect/Get : un F5 ne renvoie pas la question
        resp.sendRedirect(req.getContextPath() + "/client/assistant#fin");
    }

    @SuppressWarnings("unchecked")
    private static List<Echange> conversation(HttpSession session) {
        Object o = session.getAttribute("conversationIa");
        if (o instanceof List<?>) return (List<Echange>) o;
        List<Echange> conv = new ArrayList<>();
        session.setAttribute("conversationIa", conv);
        return conv;
    }

    /** Tout ce que l'assistant doit savoir sur le client pour répondre aux questions du sujet. */
    private String contexteClient(Utilisateur u) {
        int id = u.getIdUtilisateur();
        List<Contrat> contrats = contratDAO.findByClientAvecDetails(id);
        List<Sinistre> sinistres = sinistreDAO.findByClientAvecDetails(id);
        List<Paiement> paiements = paiementDAO.findByClientId(id);

        StringBuilder sb = new StringBuilder();
        sb.append("Client : ").append(u.getPrenom()).append(' ').append(u.getNom())
          .append(". Date du jour : ").append(WebUtil.date(java.time.LocalDate.now())).append(".\n");

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
