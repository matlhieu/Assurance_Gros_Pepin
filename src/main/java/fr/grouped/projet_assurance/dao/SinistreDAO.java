package fr.grouped.projet_assurance.dao;

import java.util.List;

import fr.grouped.projet_assurance.model.PieceJointe;
import fr.grouped.projet_assurance.model.Sinistre;

public interface SinistreDAO extends GenericDAO<Sinistre, Integer> {
    List<Sinistre> findByClient(Integer clientId);
    List<Sinistre> findByStatut(String statut);

    // Versions qui chargent aussi le contrat, le client, le type et les pièces jointes
    List<Sinistre> findAllAvecDetails();
    List<Sinistre> findByClientAvecDetails(Integer clientId);
    Sinistre findByIdAvecDetails(Integer idSinistre);

    // Pièce jointe + son sinistre/contrat/client (pour vérifier les droits avant téléchargement)
    PieceJointe findPieceJointeAvecProprietaire(Integer idPiece);

    /** Enregistre de nouvelles pièces jointes pour un sinistre existant. */
    void ajouterPiecesJointes(List<PieceJointe> pieces);
}
