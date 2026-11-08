package fr.grouped.projet_assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sinistres")
public class Sinistre {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sinistre")
    private Integer idSinistre;

    @Column(name = "numero_sinistre", unique = true, nullable = false)
    private String numeroSinistre;

    @Column(name = "date_declaration", nullable = false)
    private LocalDate dateDeclaration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "montant_estime", nullable = false)
    private BigDecimal montantEstime;

    @Column(name = "type_sinistre")
    private String typeSinistre;

    @Column(nullable = false)
    private String statut;

    @Column(name = "montant_indemnise")
    private BigDecimal montantIndemnise;

    @Column(name = "commentaire_agent", columnDefinition = "TEXT")
    private String commentaireAgent;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrat", nullable = false)
    private Contrat contrat;

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PieceJointe> piecesJointes = new ArrayList<>(); // Initialisé pour éviter le NullPointerException

    // --- GETTERS ET SETTERS ---
    
    public Integer getIdSinistre() { // Typo corrigée (I majuscule)
        return idSinistre;
    }

    public void setIdSinistre(Integer idSinistre) {
        this.idSinistre = idSinistre;
    }

    public String getNumeroSinistre() {
        return numeroSinistre;
    }

    public void setNumeroSinistre(String numeroSinistre) {
        this.numeroSinistre = numeroSinistre;
    }

    public LocalDate getDateDeclaration() {
        return dateDeclaration;
    }

    public void setDateDeclaration(LocalDate dateDeclaration) {
        this.dateDeclaration = dateDeclaration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMontantEstime() {
        return montantEstime;
    }

    // SÉCURISATION : Vérifie null et <= 0
    public void setMontantEstime(BigDecimal montantEstime) {
        if (montantEstime == null) {
            throw new IllegalArgumentException("Le montant estimé est obligatoire.");
        }
        if (montantEstime.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant estimé doit être supérieur à zéro.");
        }
        this.montantEstime = montantEstime;
    }

    public String getTypeSinistre() {
        return typeSinistre;
    }

    public void setTypeSinistre(String typeSinistre) {
        this.typeSinistre = typeSinistre;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantIndemnise() {
        return montantIndemnise;
    }

    // SÉCURISATION : Si ce n'est pas null, doit être > 0
    public void setMontantIndemnise(BigDecimal montantIndemnise) {
        if (montantIndemnise != null && montantIndemnise.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant indemnisé doit être supérieur à zéro.");
        }
        this.montantIndemnise = montantIndemnise;
    }

    public String getCommentaireAgent() {
        return commentaireAgent;
    }

    public void setCommentaireAgent(String commentaireAgent) {
        this.commentaireAgent = commentaireAgent;
    }

    // --- GETTERS ET SETTERS POUR LES RELATIONS (INDISPENSABLES) ---
    
    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public List<PieceJointe> getPiecesJointes() {
        return piecesJointes;
    }

    public void setPiecesJointes(List<PieceJointe> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }
}
