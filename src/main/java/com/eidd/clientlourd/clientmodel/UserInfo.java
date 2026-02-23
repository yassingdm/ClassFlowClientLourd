package com.eidd.clientlourd.clientmodel;

import java.util.List;

/**
 * Modèle client pour les informations utilisateur (non lié aux entités Core)
 */
public record UserInfo(String username, List<String> roles) {
}
