package com.eidd.clientlourd.clientmodel;

import java.time.Instant;

/**
 * Modèle client pour une remarque (non lié aux entités Core)
 */
public record Remarque(long id, String intitule, Long eleveId, Long classRoomId, RemarqueType type, Instant createdAt) {
}
