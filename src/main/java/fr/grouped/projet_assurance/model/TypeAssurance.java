
package fr.grouped.projet_assurance.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity // <-- AJOUTE
@Table(name = "types_assurance")
public class TypeAssurance {

    @Id // <-- AJOUTE
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- AJOUTE
    @Column(name = "id_type_assurance")
    private Integer idTypeAssurance;
    @Column(nullable = false)
    private String nom;
    private String description;
    @Column(name = "prix_base", precision = 10, scale = 2)
    private BigDecimal prixBase;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "type_garanties",
        joinColumns = @JoinColumn(name = "id_type"),
        inverseJoinColumns = @JoinColumn(name = "id_garantie")
    )
    private List<Garantie> garantiesDisponibles = new ArrayList<>();
    

    public TypeAssurance() {
    }

    public TypeAssurance(Integer idTypeAssurance, String nom, String description,
                         BigDecimal prixBase, List<Garantie> garantiesDisponibles) {
        this.idTypeAssurance = idTypeAssurance;
        this.nom = nom;
        this.description = description;
        this.prixBase = prixBase;
        this.garantiesDisponibles = garantiesDisponibles != null ? garantiesDisponibles : new ArrayList<>();
    }

    // getters / setters

    public Integer getIdTypeAssurance() { return idTypeAssurance; }
    public void setIdTypeAssurance(Integer idTypeAssurance) { this.idTypeAssurance = idTypeAssurance; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrixBase() { return prixBase; }
    public void setPrixBase(BigDecimal prixBase) { this.prixBase = prixBase; }

    public List<Garantie> getGarantiesDisponibles() { return garantiesDisponibles; }
    public void setGarantiesDisponibles(List<Garantie> garantiesDisponibles) {
        this.garantiesDisponibles = garantiesDisponibles;
    }

    public void ajouterGarantie(Garantie garantie) {
        if (garantie != null && !garantiesDisponibles.contains(garantie)) {
            garantiesDisponibles.add(garantie);
        }
    }


}