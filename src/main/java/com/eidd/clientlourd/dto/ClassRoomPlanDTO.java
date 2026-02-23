package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassRoomPlanDTO {
    private long classRoomId;
    private String classRoomNom;
    private List<TablePlanDTO> tables = new ArrayList<>();

    public ClassRoomPlanDTO() {}

    public ClassRoomPlanDTO(long classRoomId, String classRoomNom, List<TablePlanDTO> tables) {
        this.classRoomId = classRoomId;
        this.classRoomNom = classRoomNom;
        this.tables = tables;
    }

    public long getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(long classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getClassRoomNom() {
        return classRoomNom;
    }

    public void setClassRoomNom(String classRoomNom) {
        this.classRoomNom = classRoomNom;
    }

    public List<TablePlanDTO> getTables() {
        return tables;
    }

    public void setTables(List<TablePlanDTO> tables) {
        this.tables = tables;
    }
}
