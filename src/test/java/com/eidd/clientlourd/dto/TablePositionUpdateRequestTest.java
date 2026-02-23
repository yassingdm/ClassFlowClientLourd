package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour TablePositionUpdateRequest")
class TablePositionUpdateRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer une requête avec x et y")
    void shouldCreateRequestWithXAndY() {
        TablePositionUpdateRequest request = new TablePositionUpdateRequest(100, 200);
        
        assertThat(request).isNotNull();
        assertThat(request.getX()).isEqualTo(100);
        assertThat(request.getY()).isEqualTo(200);
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        TablePositionUpdateRequest request = new TablePositionUpdateRequest(50, 75);
        
        String json = objectMapper.writeValueAsString(request);
        
        assertThat(json).contains("\"x\":50");
        assertThat(json).contains("\"y\":75");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "x": 30,
                "y": 40
            }
            """;
        
        TablePositionUpdateRequest request = objectMapper.readValue(json, TablePositionUpdateRequest.class);
        
        assertThat(request).isNotNull();
        assertThat(request.getX()).isEqualTo(30);
        assertThat(request.getY()).isEqualTo(40);
    }

    @Test
    @DisplayName("Devrait accepter des coordonnées négatives")
    void shouldAcceptNegativeCoordinates() {
        TablePositionUpdateRequest request = new TablePositionUpdateRequest(-10, -20);
        
        assertThat(request.getX()).isEqualTo(-10);
        assertThat(request.getY()).isEqualTo(-20);
    }

    @Test
    @DisplayName("Devrait accepter des coordonnées à zéro")
    void shouldAcceptZeroCoordinates() {
        TablePositionUpdateRequest request = new TablePositionUpdateRequest(0, 0);
        
        assertThat(request.getX()).isEqualTo(0);
        assertThat(request.getY()).isEqualTo(0);
    }
}
