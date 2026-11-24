package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.service.IaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/client/assistant")
public class ChatIaServlet extends HttpServlet {

    private final IaService iaService = new IaService();
    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/chat-ia.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String question = req.getParameter("question");

        HttpSession session = req.getSession();
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Safe fallback to sample client ID 3 if not logged in
        int clientId = (utilisateur != null) ? utilisateur.getIdUtilisateur() : 3;

        List<Contrat> contrats = contratDAO.findByClient(clientId);
        List<Paiement> paiements = paiementDAO.findByClientId(clientId);

        // Build client context for the AI prompt
        StringBuilder context = new StringBuilder();
        if (utilisateur != null) {
            context.append("Client : ").append(utilisateur.getPrenom()).append(" ").append(utilisateur.getNom())
                   .append(" (").append(utilisateur.getEmail()).append(").\n");
        } else {
            context.append("Client : Robb Stark (robbstark@gmail.com).\n");
        }

        context.append("Contrats du client :\n");
        if (contrats.isEmpty()) {
            context.append("- Aucun contrat trouvé.\n");
        } else {
            for (Contrat c : contrats) {
                String typeNom = (c.getTypeAssurance() != null) ? c.getTypeAssurance().getNom() : "Standard";
                context.append("- N° ").append(c.getNumContrat())
                       .append(" | Formule : ").append(typeNom)
                       .append(" | Statut : ").append(c.getStatut())
                       .append(" | Prime annuelle : ").append(c.getPrime()).append(" €\n");
            }
        }

        context.append("Historique des paiements :\n");
        if (paiements.isEmpty()) {
            context.append("- Aucun paiement enregistré.\n");
        } else {
            for (Paiement p : paiements) {
                context.append("- ").append(p.getMontant()).append(" € le ").append(p.getDatePaiement())
                       .append(" (Mode : ").append(p.getModePaiement()).append(", Statut : ").append(p.getStatut()).append(")\n");
            }
        }

        String answer = iaService.askAssistant(question, context.toString());

        req.setAttribute("question", question);
        req.setAttribute("answer", answer);

        req.getRequestDispatcher("/WEB-INF/chat-ia.jsp").forward(req, resp);
    }
}
