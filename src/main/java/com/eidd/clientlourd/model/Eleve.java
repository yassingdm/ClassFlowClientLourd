package com.eidd.clientlourd.model;

import java.util.List;

public record Eleve(long id, String nom, String prenom, List<Remarque> remarques) {
}
