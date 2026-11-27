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

    private static final String API_KEY = "gsk_" + "9GsmZV22hF9X08vz1xFhWGdyb3FYVSITKTgjVWIAmFnOBVBsp7nA";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODELE = "openai/gpt-oss-20b";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String askAssistant(String question, String contexte) {
        if (question == null || question.trim().isEmpty()) {
            return "Veuillez poser une question.";
        }
        if (API_KEY.isBlank() || API_KEY.contains("METTRE_LA_SUITE_DE_LA_CLE_ICI")) {
            return "La clé API Groq n'est pas encore configurée dans IaService.java.";
        }

        String systemPrompt = "Tu es l'assistant virtuel de la compagnie d'assurance Gros Pépin. "
                + "Réponds en français, de façon claire, concise et utile. "
                + "Base-toi uniquement sur les informations ci-dessous, n'invente rien : "
                + "si une information manque, dis-le et propose de contacter un agent.";
        if (contexte != null && !contexte.isBlank()) {
            systemPrompt += "\n\n" + contexte;
        }

        try {
            Map<String, Object> body = Map.of(
                    "model", MODELE,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", question)
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                JsonNode content = root.path("choices").path(0).path("message").path("content");
                if (!content.isMissingNode() && !content.asText().isBlank()) {
                    return content.asText();
                }
                return "L'assistant a renvoyé une réponse vide, veuillez réessayer.";
            }

            return "Désolé, le service IA est momentanément indisponible (erreur HTTP "
                    + response.statusCode() + " : " + extraireErreur(response.body()) + ").";

        } catch (Exception e) {
            e.printStackTrace();
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return "Désolé, impossible de joindre le service IA pour le moment (" + detail + ").";
        }
    }

    private String extraireErreur(String body) {
        try {
            JsonNode msg = mapper.readTree(body).path("error").path("message");
            if (!msg.isMissingNode() && !msg.asText().isBlank()) {
                return msg.asText();
            }
        } catch (Exception ignored) {
        }
        return "réponse invalide";
    }
}
