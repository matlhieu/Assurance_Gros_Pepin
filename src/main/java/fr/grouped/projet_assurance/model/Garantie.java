
package fr.grouped.projet_assurance.model;

public class Garantie {

    private Long idGarantie;
    private String nom;
    private String description;

    public Garantie() {}

    public Garantie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Long getIdGarantie() {
        return idGarantie;
    }
    public void setIdGarantie(Long idGarantie) {
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