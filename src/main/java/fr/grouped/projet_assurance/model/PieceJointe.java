package fr.grouped.projet_assurance.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pieces_jointes") 
public class PieceJointe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_piece") 
    private Integer idPiece; 

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

   @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sinistre", nullable = false)
    private Sinistre sinistre;

    // Getters and setters

    public Integer getIdPiece() { 
        return idPiece; 
    }

    public void setIdPiece(Integer idPiece) { 
        this.idPiece = idPiece; 
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        if(nomFichier == null || nomFichier.isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas être vide.");
        }
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.isEmpty()) {
            throw new IllegalArgumentException("Le chemin du fichier ne peut pas être vide.");
        }
        this.cheminFichier = cheminFichier;
    }

    public Sinistre getSinistre() {
        return sinistre;
    }

    public void setSinistre(Sinistre sinistre) {
        if (sinistre == null) {
            throw new IllegalArgumentException("Le sinistre associé ne peut pas être nul.");
        }
        this.sinistre = sinistre;
    }

}