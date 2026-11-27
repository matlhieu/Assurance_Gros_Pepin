package fr.grouped.projet_assurance.servlet;

import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.PieceJointe;
import fr.grouped.projet_assurance.model.Utilisateur;
import fr.grouped.projet_assurance.util.FichierUtil;
import fr.grouped.projet_assurance.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Téléchargement d'une pièce jointe : /pieces-jointes?id=..
 * Autorisé pour un agent/admin, ou pour le client propriétaire du sinistre.
 */
@WebServlet("/pieces-jointes")
public class PieceJointeServlet extends HttpServlet {

    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilisateur u = WebUtil.utilisateurConnecte(req);
        Integer id = WebUtil.parseId(req.getParameter("id"));
        PieceJointe pj = (id == null) ? null : sinistreDAO.findPieceJointeAvecProprietaire(id);

        boolean autorise = pj != null && ("AGENT".equals(u.getRole()) || "ADMIN".equals(u.getRole())
                || pj.getSinistre().getContrat().getUtilisateur().getIdUtilisateur() == u.getIdUtilisateur());
        if (!autorise) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path fichier = FichierUtil.resoudre(pj.getCheminFichier());
        if (!Files.isRegularFile(fichier)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier introuvable sur le serveur");
            return;
        }

        String type = FichierUtil.TYPES_AUTORISES.getOrDefault(
                FichierUtil.extension(pj.getCheminFichier()), "application/octet-stream");
        String nomEncode = URLEncoder.encode(pj.getNomFichier(), StandardCharsets.UTF_8).replace("+", "%20");

        resp.setContentType(type);
        resp.setContentLengthLong(Files.size(fichier));
        // inline : PDF et images s'ouvrent dans le navigateur ; nosniff : pas d'interprétation en HTML
        resp.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + nomEncode);
        resp.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(fichier, resp.getOutputStream());
    }
}
