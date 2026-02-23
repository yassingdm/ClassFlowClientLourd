package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TablePlanDTO {
    private int x;
    private int y;
    private EleveDTO eleve;

    public TablePlanDTO() {}

    public TablePlanDTO(int x, int y, EleveDTO eleve) {
        this.x = x;
        this.y = y;
        this.eleve = eleve;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public EleveDTO getEleve() {
        return eleve;
    }

    public void setEleve(EleveDTO eleve) {
        this.eleve = eleve;
    }
}
