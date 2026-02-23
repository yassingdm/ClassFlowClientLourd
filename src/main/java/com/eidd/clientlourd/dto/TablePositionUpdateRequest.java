package com.eidd.clientlourd.dto;

public class TablePositionUpdateRequest {
    private int x;
    private int y;

    public TablePositionUpdateRequest() {}

    public TablePositionUpdateRequest(int x, int y) {
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
