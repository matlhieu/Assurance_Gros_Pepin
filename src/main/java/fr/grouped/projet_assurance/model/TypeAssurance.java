
package fr.grouped.projet_assurance.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TypeAssurance {

    private Long idTypeAssurance;
    private String nom;
    private String description;
    private BigDecimal prixBase;
    private List<Garantie> garantiesDisponibles = new ArrayList<>();

    public TypeAssurance() {
    }

    public TypeAssurance(Long idTypeAssurance, String nom, String description,
                         BigDecimal prixBase, List<Garantie> garantiesDisponibles) {
        this.idTypeAssurance = idTypeAssurance;
        this.nom = nom;
        this.description = description;
        this.prixBase = prixBase;
        this.garantiesDisponibles = garantiesDisponibles != null ? garantiesDisponibles : new ArrayList<>();
    }

    // getters / setters

    public Long getIdTypeAssurance() { return idTypeAssurance; }
    public void setIdTypeAssurance(Long idTypeAssurance) { this.idTypeAssurance = idTypeAssurance; }

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