package fr.grouped.projet_assurance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiements")
public class Paiement {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Integer idPaiement;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "mode_paiement", nullable = false)
    private String modePaiement; // CARTE, VIREMENT, PRELEVEMENT

    @Column(nullable = false)
    private String statut; // EN_ATTENTE, EFFECTUE, ECHOUE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrat", nullable = false)
    private Contrat contrat;

    // --- GETTERS ET SETTERS SÉCURISÉS ---

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    // SÉCURITÉ : Vérifie que le montant n'est pas nul et qu'il est > 0
    public void setMontant(BigDecimal montant) {
        if (montant == null) {
            throw new IllegalArgumentException("Le montant du paiement est obligatoire.");
        }
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être supérieur à zéro.");
        }
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    // SÉCURITÉ : La date ne peut pas être nulle (contrainte NOT NULL en BDD)
    public void setDatePaiement(LocalDate datePaiement) {
        if (datePaiement == null) {
            throw new IllegalArgumentException("La date de paiement est obligatoire.");
        }
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    // SÉCURITÉ : Vérifie que le mode est valide
    public void setModePaiement(String modePaiement) {
        if (modePaiement == null || modePaiement.isBlank()) {
            throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
        }
        if (!modePaiement.equals("CARTE") && !modePaiement.equals("VIREMENT") && !modePaiement.equals("PRELEVEMENT")) {
            throw new IllegalArgumentException("Mode de paiement invalide.");
        }
        this.modePaiement = modePaiement;
    }

    public String getStatut() {
        return statut;
    }

    // SÉCURITÉ : Vérifie que le statut est valide
    public void setStatut(String statut) {
        if (statut == null || statut.isBlank()) {
            throw new IllegalArgumentException("Le statut du paiement est obligatoire.");
        }
        if (!statut.equals("EN_ATTENTE") && !statut.equals("EFFECTUE") && !statut.equals("ECHOUE")) {
            throw new IllegalArgumentException("Statut de paiement invalide.");
        }
        this.statut = statut;
    }

    public Contrat getContrat() {
        return contrat;
    }

    // SÉCURITÉ : Un paiement doit obligatoirement être lié à un contrat
    public void setContrat(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le paiement doit être rattaché à un contrat.");
        }
        this.contrat = contrat;
    }
}
