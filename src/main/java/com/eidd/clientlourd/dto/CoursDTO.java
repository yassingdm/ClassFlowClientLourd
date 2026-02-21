package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoursDTO {
    private int numero;
    private Date date;

    public CoursDTO() {}

    public CoursDTO(int numero, Date date) {
        this.numero = numero;
        this.date = date;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
