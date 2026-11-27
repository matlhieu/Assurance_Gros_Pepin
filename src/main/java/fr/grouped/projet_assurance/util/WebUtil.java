package fr.grouped.projet_assurance.util;

import fr.grouped.projet_assurance.model.Utilisateur;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

/** Petites fonctions partagées par les servlets. */
public final class WebUtil {

    private WebUtil() {}

    public static Utilisateur utilisateurConnecte(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Utilisateur) session.getAttribute("utilisateur");
    }

    /** Message affiché une seule fois après une redirection (pattern Post/Redirect/Get). */
    public static void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);       // "succes" ou "erreur"
        req.getSession().setAttribute("flashMessage", message);
    }

    /** Déplace le message flash de la session vers la requête (à appeler dans doGet). */
    public static void consommerFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;
        Object msg = session.getAttribute("flashMessage");
        if (msg != null) {
            req.setAttribute("flashType", session.getAttribute("flashType"));
            req.setAttribute("flashMessage", msg);
            session.removeAttribute("flashType");
            session.removeAttribute("flashMessage");
        }
    }

    /** Entier strictement positif, ou null si le paramètre est absent/invalide. */
    public static Integer parseId(String valeur) {
        if (valeur == null) return null;
        try {
            int id = Integer.parseInt(valeur.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Montant positif (accepte la virgule française), ou null si invalide. */
    public static BigDecimal parseMontant(String valeur) {
        if (valeur == null || valeur.isBlank()) return null;
        try {
            BigDecimal m = new BigDecimal(valeur.trim().replace(',', '.'));
            return m.compareTo(BigDecimal.ZERO) > 0 ? m.setScale(2, java.math.RoundingMode.HALF_UP) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ---------- Affichage (appelables depuis les JSP : ${WebUtil.date(x)}) ----------

    private static final java.time.format.DateTimeFormatter FORMAT_DATE =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** 2026-03-05 -> 05/03/2026 */
    public static String date(java.time.LocalDate d) {
        return d == null ? "–" : d.format(FORMAT_DATE);
    }

    /** 1234.5 -> 1 234,50 € */
    public static String euros(BigDecimal montant) {
        if (montant == null) return "–";
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.FRANCE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(montant) + " €";
    }

    /** Libellé lisible d'un statut de sinistre ou de paiement. */
    public static String libelle(String statut) {
        if (statut == null) return "–";
        return switch (statut) {
            case "DECLARE" -> "Déclaré";
            case "EN_ANALYSE" -> "En analyse";
            case "ACCEPTE" -> "Accepté";
            case "REFUSE" -> "Refusé";
            case "INDEMNISE" -> "Indemnisé";
            case "EN_ATTENTE" -> "En attente";
            case "EFFECTUE" -> "Effectué";
            case "ECHOUE" -> "Échoué";
            case "CARTE" -> "Carte bancaire";
            case "VIREMENT" -> "Virement";
            case "PRELEVEMENT" -> "Prélèvement";
            default -> statut;
        };
    }

    /** Initiales pour l'avatar : "Jean", "Dupont" -> "JD" */
    public static String initiales(String prenom, String nom) {
        String a = (prenom == null || prenom.isBlank()) ? "" : prenom.trim().substring(0, 1);
        String b = (nom == null || nom.isBlank()) ? "" : nom.trim().substring(0, 1);
        return (a + b).toUpperCase(java.util.Locale.FRANCE);
    }

    /** 2026-12-01 -> "déc. 2026" (pour les blocs date des échéances) */
    public static String moisAnnee(java.time.LocalDate d) {
        return d == null ? "" : d.format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy", java.util.Locale.FRANCE));
    }

    /** 3 -> "000003" */
    public static String numeroClient(int id) {
        return String.format("%06d", id);
    }

    /**
     * Met en forme une réponse de l'assistant IA de façon sûre :
     * le texte est d'abord échappé (aucun HTML venant de l'IA n'est interprété),
     * puis **gras**, listes "- " / "* " et retours à la ligne sont convertis.
     */
    public static String texteIa(String texte) {
        if (texte == null) return "";
        String s = texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                        .replace("\"", "&quot;").replace("'", "&#39;");
        s = s.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
        StringBuilder out = new StringBuilder();
        boolean dansListe = false;
        for (String ligne : s.split("\\R")) {
            String l = ligne.strip();
            if (l.startsWith("* ") || l.startsWith("- ")) {
                if (!dansListe) { out.append("<ul>"); dansListe = true; }
                out.append("<li>").append(l.substring(2)).append("</li>");
            } else {
                if (dansListe) { out.append("</ul>"); dansListe = false; }
                if (!l.isEmpty()) out.append("<p>").append(l).append("</p>");
            }
        }
        if (dansListe) out.append("</ul>");
        return out.toString();
    }
}
