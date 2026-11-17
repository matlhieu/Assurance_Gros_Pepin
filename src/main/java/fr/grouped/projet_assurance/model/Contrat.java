package com.assurance.model;

import com.assurance.model.enums.StatutContrat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Contrat {

    private Long idContrat;
    private String numContrat;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal prime;
    private StatutContrat statut;
    private Utilisateur client;
    private TypeAssurance typeAssurance;
    private List<Garantie> garanties = new ArrayList<>();

    public Contrat() {
        this.statut = StatutContrat.EN_ATTENTE;
        this.dateDebut = LocalDate.now();
    }

    public Contrat(Long idContrat, String numContrat, LocalDate dateDebut, LocalDate dateFin,
                   BigDecimal prime, StatutContrat statut, Utilisateur client,
                   TypeAssurance typeAssurance, List<Garantie> garanties) {
        this.idContrat = idContrat;
        this.numContrat = numContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prime = prime;
        this.statut = statut;
        this.client = client;
        this.typeAssurance = typeAssurance;
        this.garanties = garanties != null ? garanties : new ArrayList<>();
    }

    // getters / setters

    public Long getIdContrat() { return idContrat; }
    public void setIdContrat(Long idContrat) { this.idContrat = idContrat; }

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

    public Utilisateur getClient() { return client; }
    public void setClient(Utilisateur client) { this.client = client; }

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