package com.eidd.clientlourd.dto;

public enum RemarqueType {
    REMARQUE_GENERALE,
    DEVOIR_NON_FAIT,
    BAVARDAGE;
    
    @Override
    public String toString() {
        return switch (this) {
            case REMARQUE_GENERALE -> "Remarque générale";
            case DEVOIR_NON_FAIT -> "Devoir non fait";
            case BAVARDAGE -> "Bavardage";
        };
    }
}
