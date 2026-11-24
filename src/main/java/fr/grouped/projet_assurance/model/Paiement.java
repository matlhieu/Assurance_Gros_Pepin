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
    private String modePaiement;

    @Column(nullable = false)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrat", nullable = false)
    private Contrat contrat;

    public Paiement() {
    }

    public Paiement(BigDecimal montant, LocalDate datePaiement, String modePaiement, String statut, Contrat contrat) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.contrat = contrat;
    }

    public Paiement(BigDecimal montant, LocalDate datePaiement, String modePaiement, String statut, int idContrat) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.contrat = new Contrat();
        this.contrat.setIdContrat(idContrat);
    }

    public Paiement(double montant, LocalDate datePaiement, String modePaiement, String statut, int idContrat) {
        this(BigDecimal.valueOf(montant), datePaiement, modePaiement, statut, idContrat);
    }

    public Integer getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Integer idPaiement) {
        this.idPaiement = idPaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

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

    public void setDatePaiement(LocalDate datePaiement) {
        if (datePaiement == null) {
            throw new IllegalArgumentException("La date de paiement est obligatoire.");
        }
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

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

    public void setContrat(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Le paiement doit être rattaché à un contrat.");
        }
        this.contrat = contrat;
    }

    public Integer getIdContrat() {
        return contrat != null ? contrat.getIdContrat() : null;
    }

    public void setIdContrat(Integer idContrat) {
        if (idContrat != null) {
            if (this.contrat == null) {
                this.contrat = new Contrat();
            }
            this.contrat.setIdContrat(idContrat);
        }
    }
}
