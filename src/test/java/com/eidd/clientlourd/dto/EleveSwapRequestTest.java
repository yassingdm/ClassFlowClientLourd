package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour EleveSwapRequest")
class EleveSwapRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer une requête avec deux IDs d'élèves")
    void shouldCreateRequestWithTwoEleveIds() {
        EleveSwapRequest request = new EleveSwapRequest(1L, 2L);
        
        assertThat(request).isNotNull();
        assertThat(request.getEleveId1()).isEqualTo(1L);
        assertThat(request.getEleveId2()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        EleveSwapRequest request = new EleveSwapRequest(10L, 20L);
        
        String json = objectMapper.writeValueAsString(request);
        
        assertThat(json).contains("\"eleveId1\":10");
        assertThat(json).contains("\"eleveId2\":20");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "eleveId1": 100,
                "eleveId2": 200
            }
            """;
        
        EleveSwapRequest request = objectMapper.readValue(json, EleveSwapRequest.class);
        
        assertThat(request).isNotNull();
        assertThat(request.getEleveId1()).isEqualTo(100L);
        assertThat(request.getEleveId2()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Devrait accepter les mêmes IDs pour les deux élèves")
    void shouldAcceptSameIdsForBothEleves() {
        EleveSwapRequest request = new EleveSwapRequest(5L, 5L);
        
        assertThat(request.getEleveId1()).isEqualTo(5L);
        assertThat(request.getEleveId2()).isEqualTo(5L);
    }
}
