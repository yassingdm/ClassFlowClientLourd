package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemarqueRequestDTO {
    private String intitule;
    private Long eleveId;
    private Long classRoomId;
    private RemarqueType type;

    public RemarqueRequestDTO() {}

    public RemarqueRequestDTO(String intitule, Long eleveId, Long classRoomId, RemarqueType type) {
        this.intitule = intitule;
        this.eleveId = eleveId;
        this.classRoomId = classRoomId;
        this.type = type;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public Long getEleveId() {
        return eleveId;
    }

    public void setEleveId(Long eleveId) {
        this.eleveId = eleveId;
    }

    public Long getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(Long classRoomId) {
        this.classRoomId = classRoomId;
    }

    public RemarqueType getType() {
        return type;
    }

    public void setType(RemarqueType type) {
        this.type = type;
    }
}
