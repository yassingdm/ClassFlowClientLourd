package com.eidd.clientlourd.dto;

public class EleveSwapRequest {
    private long eleveId1;
    private long eleveId2;

    public EleveSwapRequest() {}

    public EleveSwapRequest(long eleveId1, long eleveId2) {
        this.eleveId1 = eleveId1;
        this.eleveId2 = eleveId2;
    }

    public long getEleveId1() {
        return eleveId1;
    }

    public void setEleveId1(long eleveId1) {
        this.eleveId1 = eleveId1;
    }

    public long getEleveId2() {
        return eleveId2;
    }

    public void setEleveId2(long eleveId2) {
        this.eleveId2 = eleveId2;
    }
}
