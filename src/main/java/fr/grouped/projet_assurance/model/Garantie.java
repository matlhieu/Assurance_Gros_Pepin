
package fr.grouped.projet_assurance.model;
import jakarta.persistence.*;


@Entity // <-- AJOUTE
@Table(name = "garanties") 
public class Garantie {

    @Id // <-- AJOUTE
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- AJOUTE
    @Column(name = "id_garantie")
    private Integer idGarantie;
    @Column(nullable = false)
    private String nom;
    private String description;

    public Garantie() {}

    public Garantie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Integer getIdGarantie() {
        return idGarantie;
    }
    public void setIdGarantie(Integer idGarantie) {
        this.idGarantie = idGarantie;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}