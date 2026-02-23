package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO pour la création de groupes aléatoires
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeRandomCreateRequest {
    private int groupCount;

    public GroupeRandomCreateRequest() {}

    public GroupeRandomCreateRequest(int groupCount) {
        this.groupCount = groupCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
}
