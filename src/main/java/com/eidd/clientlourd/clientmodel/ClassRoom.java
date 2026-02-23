package com.eidd.clientlourd.clientmodel;

import java.util.List;

/**
 * Modèle client pour une salle de classe (non lié aux entités Core)
 */
public record ClassRoom(long id, String nom, List<Eleve> eleves, List<Table> tables) {
}
