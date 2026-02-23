package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO pour la création de groupes manuels
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeCreateRequest {
    private java.util.List<java.util.List<Long>> groupes;
    private java.util.List<String> noms;

    public GroupeCreateRequest() {}

    public GroupeCreateRequest(java.util.List<java.util.List<Long>> groupes) {
        this.groupes = groupes;
    }

    public java.util.List<java.util.List<Long>> getGroupes() {
        return groupes;
    }

    public void setGroupes(java.util.List<java.util.List<Long>> groupes) {
        this.groupes = groupes;
    }

    public java.util.List<String> getNoms() {
        return noms;
    }

    public void setNoms(java.util.List<String> noms) {
        this.noms = noms;
    }
}
