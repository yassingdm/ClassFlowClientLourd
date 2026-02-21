package com.eidd.clientlourd.model;

import java.time.Instant;

public record Remarque(long id, String intitule, Long eleveId, Long classRoomId, RemarqueType type, Instant createdAt) {
}
