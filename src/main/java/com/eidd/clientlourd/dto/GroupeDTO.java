package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeDTO {
    private long id;
    private String nom;
    private List<EleveDTO> eleves = new ArrayList<>();

    public GroupeDTO() {}

    public GroupeDTO(long id, List<EleveDTO> eleves) {
        this.id = id;
        this.eleves = eleves;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<EleveDTO> getEleves() {
        return eleves;
    }

    public void setEleves(List<EleveDTO> eleves) {
        this.eleves = eleves;
    }

    public void addEleve(EleveDTO eleve) {
        this.eleves.add(eleve);
    }

    public void removeEleve(EleveDTO eleve) {
        this.eleves.remove(eleve);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
