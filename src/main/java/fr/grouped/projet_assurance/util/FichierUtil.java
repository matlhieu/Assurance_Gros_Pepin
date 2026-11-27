package fr.grouped.projet_assurance.util;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Stockage des pièces jointes HORS du dossier web (elles ne sont donc jamais servies directement).
 * Dossier : propriété système "assurance.uploads", sinon ~/assurance-uploads.
 */
public final class FichierUtil {

    public static final long TAILLE_MAX = 5L * 1024 * 1024; // 5 Mo par fichier

    /** Extensions autorisées -> type MIME renvoyé au téléchargement. */
    public static final Map<String, String> TYPES_AUTORISES = Map.of(
            "pdf", "application/pdf",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png");

    private FichierUtil() {}

    public static Path dossier() throws IOException {
        String conf = System.getProperty("assurance.uploads");
        Path dir = (conf != null && !conf.isBlank())
                ? Paths.get(conf)
                : Paths.get(System.getProperty("user.home"), "assurance-uploads");
        Files.createDirectories(dir);
        return dir.toAbsolutePath().normalize();
    }

    /** Extension en minuscules ("" si aucune). */
    public static String extension(String nomFichier) {
        if (nomFichier == null) return "";
        int i = nomFichier.lastIndexOf('.');
        return i < 0 ? "" : nomFichier.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    /** Nom d'origine nettoyé (sans chemin), pour l'affichage uniquement. */
    public static String nomAffichable(Part part) {
        String nom = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        nom = nom.replaceAll("[^\\p{L}\\p{N}._ -]", "_");
        return nom.length() > 200 ? nom.substring(nom.length() - 200) : nom;
    }

    /** Vérifie extension, taille et signature réelle du fichier. Renvoie un message d'erreur ou null. */
    public static String verifier(Part part) throws IOException {
        String ext = extension(part.getSubmittedFileName());
        if (!TYPES_AUTORISES.containsKey(ext)) {
            return "Format non autorisé (" + part.getSubmittedFileName() + ") : PDF, JPG ou PNG uniquement.";
        }
        if (part.getSize() > TAILLE_MAX) {
            return "Fichier trop volumineux (" + part.getSubmittedFileName() + ") : 5 Mo maximum.";
        }
        if (!signatureValide(part, ext)) {
            return "Le contenu de " + part.getSubmittedFileName() + " ne correspond pas à son extension.";
        }
        return null;
    }

    /** Enregistre le fichier sous un nom aléatoire et renvoie ce nom (à stocker en base). */
    public static String enregistrer(Part part) throws IOException {
        String nomStocke = UUID.randomUUID() + "." + extension(part.getSubmittedFileName());
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, dossier().resolve(nomStocke));
        }
        return nomStocke;
    }

    /** Résout un nom stocké en chemin, en refusant toute sortie du dossier (path traversal). */
    public static Path resoudre(String nomStocke) throws IOException {
        Path dir = dossier();
        Path fichier = dir.resolve(nomStocke).normalize();
        if (!fichier.startsWith(dir)) {
            throw new IOException("Chemin de fichier invalide");
        }
        return fichier;
    }

    public static void supprimerSilencieusement(String nomStocke) {
        try {
            Files.deleteIfExists(resoudre(nomStocke));
        } catch (IOException ignored) {
            // rien à faire : nettoyage best-effort
        }
    }

    /** Compare les premiers octets ("magic numbers") avec le format annoncé. */
    private static boolean signatureValide(Part part, String ext) throws IOException {
        byte[] debut = new byte[8];
        int lus;
        try (InputStream in = part.getInputStream()) {
            lus = in.readNBytes(debut, 0, debut.length);
        }
        return switch (ext) {
            case "pdf" -> lus >= 4 && debut[0] == '%' && debut[1] == 'P' && debut[2] == 'D' && debut[3] == 'F';
            case "png" -> lus >= 8 && (debut[0] & 0xFF) == 0x89 && debut[1] == 'P' && debut[2] == 'N' && debut[3] == 'G';
            case "jpg", "jpeg" -> lus >= 3 && (debut[0] & 0xFF) == 0xFF && (debut[1] & 0xFF) == 0xD8 && (debut[2] & 0xFF) == 0xFF;
            default -> false;
        };
    }
}
