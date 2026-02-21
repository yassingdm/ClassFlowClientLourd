package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeDTO {
    private List<EleveDTO> eleves = new ArrayList<>();

    public GroupeDTO() {}

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
}
