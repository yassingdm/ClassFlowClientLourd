package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour PositionDTO")
class PositionDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer une position avec constructeur par défaut")
    void shouldCreatePositionWithDefaultConstructor() {
        PositionDTO position = new PositionDTO();
        
        assertThat(position).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer une position avec x et y")
    void shouldCreatePositionWithXAndY() {
        PositionDTO position = new PositionDTO(100, 200);
        
        assertThat(position.getX()).isEqualTo(100);
        assertThat(position.getY()).isEqualTo(200);
    }

    @Test
    @DisplayName("Devrait définir et récupérer x")
    void shouldSetAndGetX() {
        PositionDTO position = new PositionDTO();
        position.setX(50);
        
        assertThat(position.getX()).isEqualTo(50);
    }

    @Test
    @DisplayName("Devrait définir et récupérer y")
    void shouldSetAndGetY() {
        PositionDTO position = new PositionDTO();
        position.setY(75);
        
        assertThat(position.getY()).isEqualTo(75);
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        PositionDTO position = new PositionDTO(10, 20);
        
        String json = objectMapper.writeValueAsString(position);
        
        assertThat(json).contains("\"x\":10");
        assertThat(json).contains("\"y\":20");
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
        
        PositionDTO position = objectMapper.readValue(json, PositionDTO.class);
        
        assertThat(position).isNotNull();
        assertThat(position.getX()).isEqualTo(30);
        assertThat(position.getY()).isEqualTo(40);
    }

    @Test
    @DisplayName("Devrait accepter des coordonnées négatives")
    void shouldAcceptNegativeCoordinates() {
        PositionDTO position = new PositionDTO(-10, -20);
        
        assertThat(position.getX()).isEqualTo(-10);
        assertThat(position.getY()).isEqualTo(-20);
    }

    @Test
    @DisplayName("Devrait ignorer les propriétés inconnues")
    void shouldIgnoreUnknownProperties() throws IOException {
        String json = """
            {
                "x": 10,
                "y": 20,
                "unknownProperty": "value"
            }
            """;
        
        assertThatCode(() -> objectMapper.readValue(json, PositionDTO.class))
            .doesNotThrowAnyException();
    }
}
