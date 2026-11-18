package fr.grouped.projet_assurance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class IaService {

    private static final String API_KEY = "AQ.Ab8RN6LdWgl_EQMJl0p10YJuA8QSAoKCA8dE2lQ907Lcfse-kA";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public String askAssistant(String userQuestion, String clientContext) {
        try {
            String fullPrompt = "Tu es l'assistant virtuel d'une compagnie d'assurance. "
                    + "Reponds de maniere claire et courte en te basant sur les infos du client.\n"
                    + "Infos client : " + clientContext + "\n"
                    + "Question : " + userQuestion;

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> bodyMap = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", fullPrompt)))
                    )
            );
            String jsonBody = mapper.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isMissingNode()) {
                return "Erreur API : " + response.body();
            }
            return textNode.asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'appel a l'IA.";
        }
    }
}