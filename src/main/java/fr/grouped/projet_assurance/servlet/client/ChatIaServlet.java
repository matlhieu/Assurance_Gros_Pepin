package fr.grouped.projet_assurance.servlet.client;

import fr.grouped.projet_assurance.service.IaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/client/assistant")
public class ChatIaServlet extends HttpServlet {

        private IaService iaService = new IaService();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.getRequestDispatcher("/WEB-INF/chat-ia.jsp").forward(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String question = req.getParameter("question");

            // static context for now (replace with real contracts from ContratDao later)
            String clientContext = "Contrat Auto Tous Risques (Actif, 55 EUR/mois, franchise 300 EUR). "
                    + "Contrat Habitation (Actif, 25 EUR/mois, degats des eaux inclus).";

            String answer = iaService.askAssistant(question, clientContext);

            req.setAttribute("question", question);
            req.setAttribute("answer", answer);
            req.getRequestDispatcher("/WEB-INF/chat-ia.jsp").forward(req, resp);
        }
}

