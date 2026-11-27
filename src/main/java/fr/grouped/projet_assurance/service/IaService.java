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

/**
 * Appel au modèle d'IA qui fait répondre l'assistant Gros Pépin.
 *
 * POURQUOI L'ANCIENNE VERSION NE RÉPONDAIT PLUS (diagnostic du 27/11/2026) :
 *  1. Le modèle « gemini-2.5-flash » a été retiré par Google : l'API renvoie 404
 *     « This model is no longer available to new users ». Le code d'avant traitait
 *     ce cas en silence et affichait le texte de secours « Service IA momentanément
 *     indisponible » au lieu d'une vraie réponse.
 *  2. Depuis la France (et la plupart des pays d'Europe), l'API Gemini gratuite
 *     renvoie en plus « User location is not supported for the API use » (vérifié).
 *
 * La solution : on appelle Mistral AI (fournisseur français, gratuit, sans blocage
 * géographique en Europe) par défaut, et on rend tout configurable par variables
 * d'environnement pour ne plus jamais dépendre d'un modèle figé dans le code :
 *
 *   IA_FOURNISSEUR : « mistral » (défaut) ou « gemini »
 *   IA_CLE_API     : la clé API
 *                    - Mistral : https://console.mistral.ai (gratuit, plan « free »)
 *                    - Gemini  : https://aistudio.google.com (si votre région est acceptée)
 *   IA_MODELE      : nom du modèle (défaut : mistral-small-latest, ou gemini-3.8-flash pour Gemini)
 *
 * Exemple de configuration (IntelliJ > Run Configuration > Environment variables,
 * ou fichier setenv.sh / setenv.bat de Tomcat) :
 *   IA_CLE_API=xxxxx
 *
 * IMPORTANT : ne jamais committer la clé dans le dépôt (l'ancienne clé Gemini a
 * déjà fuité dans l'historique git : pensez à la révoquer sur aistudio.google.com).
 */
public class IaService {

    private static final String FOURNISSEUR = envOuDefaut("IA_FOURNISSEUR", "mistral").trim().toLowerCase();
    private static final String CLE_API = envOuDefaut("IA_CLE_API", "").trim();
    private static final String MODELE = envOuDefaut("IA_MODELE",
            "gemini".equals(FOURNISSEUR) ? "gemini-3.8-flash" : "mistral-small-latest").trim();

    private static final String URL_MISTRAL = "https://api.mistral.ai/v1/chat/completions";
    private static final String URL_GEMINI = "https://generativelanguage.googleapis.com/v1beta/models/" + MODELE + ":generateContent";

    private static final String CONSIGNE_SYSTEME =
            "Tu es l'assistant virtuel de la compagnie d'assurance Gros Pépin. "
            + "Réponds en français, de façon claire, concise et utile. "
            + "Bases-toi uniquement sur les informations transmises ci-dessous, n'invente rien : "
            + "si une information manque, dis-le et propose de contacter un agent.";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    private static String envOuDefaut(String nom, String defaut) {
        String v = System.getenv(nom);
        if (v == null || v.isBlank()) v = System.getProperty(nom); // accepte aussi -DIA_CLE_API=...
        return (v == null || v.isBlank()) ? defaut : v;
    }

    /** Pose la question à l'IA avec le contexte préparé par la servlet. Renvoie toujours un texte affichable. */
    public String askAssistant(String question, String contexte) {
        if (question == null || question.trim().isEmpty()) {
            return "Veuillez poser une question.";
        }
        if (CLE_API.isBlank()) {
            return "L'assistant n'est pas encore configuré côté serveur : la clé API est absente.\n"
                    + "Définissez la variable d'environnement IA_CLE_API (voir les commentaires de IaService.java), "
                    + "puis redémarrez Tomcat.";
        }

        String contenuUtilisateur = contexte + "\n\nQuestion : " + question;
        try {
            return "gemini".equals(FOURNISSEUR) ? appelerGemini(contenuUtilisateur) : appelerMistral(contenuUtilisateur);
        } catch (Exception e) {
            e.printStackTrace(); // trace complète dans les logs de Tomcat
            return "Désolé, je n'arrive pas à joindre le service IA pour le moment.\n"
                    + "(détail technique : " + court(e.getMessage()) + ")";
        }
    }

    /** Mistral : POST /v1/chat/completions avec un tableau « messages », réponse dans choices[0].message.content. */
    private String appelerMistral(String contenu) throws Exception {
        Map<String, Object> body = Map.of(
                "model", MODELE,
                "temperature", 0.4,
                "messages", List.of(
                        Map.of("role", "system", "content", CONSIGNE_SYSTEME),
                        Map.of("role", "user", "content", contenu)
                ));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_MISTRAL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + CLE_API)
                .timeout(Duration.ofSeconds(45))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode texte = mapper.readTree(response.body()).path("choices").path(0).path("message").path("content");
            if (!texte.isMissingNode() && !texte.asText().isBlank()) return texte.asText();
            return "L'assistant a renvoyé une réponse vide, réessayez.";
        }
        throw new IllegalStateException("Mistral HTTP " + response.statusCode() + " : " + court(messageErreur(response.body())));
    }

    /** Gemini : POST …/models/<modèle>:generateContent avec la clé dans l'en-tête x-goog-api-key. */
    private String appelerGemini(String contenu) throws Exception {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", CONSIGNE_SYSTEME))),
                "contents", List.of(Map.of("parts", List.of(Map.of("text", contenu)))));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_GEMINI))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", CLE_API)
                .timeout(Duration.ofSeconds(45))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode texte = mapper.readTree(response.body())
                    .path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (!texte.isMissingNode() && !texte.asText().isBlank()) return texte.asText();
            return "L'assistant a renvoyé une réponse vide, réessayez.";
        }
        throw new IllegalStateException("Gemini HTTP " + response.statusCode() + " : " + court(messageErreur(response.body())));
    }

    /** Extrait le champ error.message du JSON renvoyé par l'API en cas d'échec. */
    private String messageErreur(String corpsJson) {
        try {
            JsonNode msg = mapper.readTree(corpsJson).path("error").path("message");
            if (!msg.isMissingNode()) return msg.asText();
        } catch (Exception ignore) { /* corps non JSON : on renverra le brut */ }
        return corpsJson;
    }

    /** Message court lisible dans la bulle de chat (le détail complet va dans les logs). */
    private static String court(String texte) {
        if (texte == null) return "erreur inconnue";
        String t = texte.replaceAll("\\s+", " ").trim();
        return t.length() > 160 ? t.substring(0, 160) + "…" : t;
    }
}
