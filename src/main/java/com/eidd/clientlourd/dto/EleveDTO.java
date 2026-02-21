package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EleveDTO {
    private long id;
    private String nom;
    private String prenom;
    private List<RemarqueDTO> remarques = new ArrayList<>();

    public EleveDTO() {}

    public EleveDTO(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public List<RemarqueDTO> getRemarques() {
        return remarques;
    }

    public void setRemarques(List<RemarqueDTO> remarques) {
        this.remarques = remarques;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
