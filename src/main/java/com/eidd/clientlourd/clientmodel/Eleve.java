package com.eidd.clientlourd.clientmodel;

import java.util.List;

/**
 * Modèle client pour un élève (non lié aux entités Core)
 */
public record Eleve(long id, String nom, String prenom, List<Remarque> remarques) {
}
