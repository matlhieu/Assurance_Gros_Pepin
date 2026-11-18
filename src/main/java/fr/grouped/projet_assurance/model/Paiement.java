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
    private int idPaiement;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "mode_paiement", nullable = false)
    private String modePaiement;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "id_contrat", nullable = false)
    private int idContrat;

    public Paiement() {
    }

    public Paiement(BigDecimal montant, LocalDate datePaiement, String modePaiement, String statut, int idContrat) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.idContrat = idContrat;
    }

    public Paiement(double montant, LocalDate datePaiement, String modePaiement, String statut, int idContrat) {
        this(BigDecimal.valueOf(montant), datePaiement, modePaiement, statut, idContrat);
    }

    public int getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(int idPaiement) {
        this.idPaiement = idPaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getIdContrat() {
        return idContrat;
    }

    public void setIdContrat(int idContrat) {
        this.idContrat = idContrat;
    }
}
