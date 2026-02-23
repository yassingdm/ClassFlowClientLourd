package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO pour la mise à jour d'un groupe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeUpdateRequest {
    private List<Long> addEleveIds;
    private List<Long> removeEleveIds;
    private String nom;

    public GroupeUpdateRequest() {}

    public GroupeUpdateRequest(List<Long> addEleveIds, List<Long> removeEleveIds) {
        this.addEleveIds = addEleveIds;
        this.removeEleveIds = removeEleveIds;
    }

    public List<Long> getAddEleveIds() {
        return addEleveIds;
    }

    public void setAddEleveIds(List<Long> addEleveIds) {
        this.addEleveIds = addEleveIds;
    }

    public List<Long> getRemoveEleveIds() {
        return removeEleveIds;
    }

    public void setRemoveEleveIds(List<Long> removeEleveIds) {
        this.removeEleveIds = removeEleveIds;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
