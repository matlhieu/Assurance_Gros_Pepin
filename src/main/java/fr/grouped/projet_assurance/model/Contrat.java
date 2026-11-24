
package fr.grouped.projet_assurance.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.grouped.projet_assurance.model.enums.StatutContrat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "contrats")
public class Contrat {

    @Id // <--- IL MANQUE CETTE LIGNE
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <--- ET CELLE-CI AUSSI
    @Column(name = "id_contrat") 
    private Integer idContrat;
    @Column(name = "date_debut", nullable = false) // <-- AJOUTE
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false) // <-- AJOUTE
    private LocalDate dateFin;

    @Column(name = "numero_contrat", nullable = false, unique = true) // <-- AJOUTE
    private String numContrat;
    @Column(nullable = false)
    private BigDecimal prime;
    @Enumerated(EnumType.STRING) // <-- AJOUTE CETTE LIGNE
    @Column(nullable = false)
    private StatutContrat statut;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeAssurance typeAssurance;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contrat_garanties",
        joinColumns = @JoinColumn(name = "id_contrat"),
        inverseJoinColumns = @JoinColumn(name = "id_garantie")
    )
    private List<Garantie> garanties = new ArrayList<>();

    public Contrat() {
        this.statut = StatutContrat.EN_ATTENTE;
        this.dateDebut = LocalDate.now();
    }

    public Contrat(Integer idContrat, String numContrat, LocalDate dateDebut, LocalDate dateFin,
                   BigDecimal prime, StatutContrat statut, Utilisateur utilisateur,
                   TypeAssurance typeAssurance, List<Garantie> garanties) {
        this.idContrat = idContrat;
        this.numContrat = numContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prime = prime;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.typeAssurance = typeAssurance;
        this.garanties = garanties != null ? garanties : new ArrayList<>();
    }

    // getters / setters

    public Integer getIdContrat() { return idContrat; }
    public void setIdContrat(Integer idContrat) { this.idContrat = idContrat; }

    public String getNumContrat() { return numContrat; }
    public void setNumContrat(String numContrat) { this.numContrat = numContrat; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public BigDecimal getPrime() { return prime; }
    public void setPrime(BigDecimal prime) { this.prime = prime; }

    public StatutContrat getStatut() { return statut; }
    public void setStatut(StatutContrat statut) { this.statut = statut; }

   

    public TypeAssurance getTypeAssurance() { return typeAssurance; }
    public void setTypeAssurance(TypeAssurance typeAssurance) { this.typeAssurance = typeAssurance; }

    public List<Garantie> getGaranties() { return garanties; }
    public void setGaranties(List<Garantie> garanties) {
        if (garanties != null) {
            this.garanties = garanties;
        } else {
            this.garanties = new ArrayList<>();
        }
    }
    
}