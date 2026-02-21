package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassRoomDTO {
    private long id;
    private String nom;
    private List<EleveDTO> eleves = new ArrayList<>();
    private List<TableDTO> tables = new ArrayList<>();

    public ClassRoomDTO() {}

    public ClassRoomDTO(String nom) {
        this.nom = nom;
        this.eleves = new ArrayList<>();
        this.tables = new ArrayList<>();
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

    public List<EleveDTO> getEleves() {
        return eleves;
    }

    public void setEleves(List<EleveDTO> eleves) {
        this.eleves = eleves;
    }

    public List<TableDTO> getTables() {
        return tables;
    }

    public void setTables(List<TableDTO> tables) {
        this.tables = tables;
    }
}
