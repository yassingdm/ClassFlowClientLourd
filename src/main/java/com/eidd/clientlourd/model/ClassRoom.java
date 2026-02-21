package com.eidd.clientlourd.model;

import java.util.List;

public record ClassRoom(long id, String nom, List<Eleve> eleves, List<Table> tables) {
}
