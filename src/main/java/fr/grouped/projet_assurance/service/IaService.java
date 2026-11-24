package fr.grouped.projet_assurance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IaService {

    private static final String API_KEY = "AQ.Ab8RN6LdWgl_EQMJl0p10YJuA8QSAoKCA8dE2lQ907Lcfse-kA";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String askAssistant(String userQuestion, String clientContext) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return "Veuillez poser une question.";
        }

        try {
            String fullPrompt = "Tu es l'assistant virtuel d'une compagnie d'assurance (Gros Pépin). "
                    + "Réponds en français de manière claire et concise en te basant sur les informations du client.\n"
                    + "Informations client (contrats et paiements) : " + clientContext + "\n"
                    + "Question : " + userQuestion;

            Map<String, Object> bodyMap = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", fullPrompt)))
                    )
            );
            String jsonBody = mapper.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                if (!textNode.isMissingNode()) {
                    return textNode.asText();
                }
            }

            return "Bonjour ! Je suis votre assistant virtuel Gros Pépin.\n"
                    + "Voici les données de votre dossier :\n" + clientContext + "\n\n"
                    + "Votre question : \"" + userQuestion + "\"\n"
                    + "(Remarque : Service IA momentanément indisponible).";

        } catch (Exception e) {
            e.printStackTrace();
            return "Bonjour ! Je suis votre assistant virtuel Gros Pépin.\n"
                    + "Voici les données de votre dossier :\n" + clientContext + "\n\n"
                    + "(Erreur de communication avec le service IA : " + e.getMessage() + ")";
        }
    }
}
