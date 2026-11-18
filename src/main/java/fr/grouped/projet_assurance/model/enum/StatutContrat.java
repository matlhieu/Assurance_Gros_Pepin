package fr.grouped.projet_assurance.model.enums;

public enum StatutContrat {
    EN_ATTENTE("En attente"),
    ACTIF("Actif"),
    SUSPENDU("Suspendu"),
    EXPIRE("Expiré"),
    RESILIE("Résilié");

    private final String libelle;

    StatutContrat(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}