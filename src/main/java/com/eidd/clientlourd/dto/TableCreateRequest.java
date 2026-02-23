package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO pour la création d'une table
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableCreateRequest {
    private int x;
    private int y;

    public TableCreateRequest() {}

    public TableCreateRequest(int x, int y) {
        this.x = x;
        this.y = y;
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
}
