package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour TableDTO")
class TableDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer une table avec constructeur par défaut")
    void shouldCreateTableWithDefaultConstructor() {
        TableDTO table = new TableDTO();
        
        assertThat(table).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer une table avec une position")
    void shouldCreateTableWithPosition() {
        PositionDTO position = new PositionDTO(10, 20);
        TableDTO table = new TableDTO(position);
        
        assertThat(table.getPosition()).isNotNull();
        assertThat(table.getPosition().getX()).isEqualTo(10);
        assertThat(table.getPosition().getY()).isEqualTo(20);
    }

    @Test
    @DisplayName("Devrait définir et récupérer la position")
    void shouldSetAndGetPosition() {
        TableDTO table = new TableDTO();
        PositionDTO position = new PositionDTO(5, 15);
        
        table.setPosition(position);
        
        assertThat(table.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        PositionDTO position = new PositionDTO(100, 200);
        TableDTO table = new TableDTO(position);
        
        String json = objectMapper.writeValueAsString(table);
        
        assertThat(json).contains("\"position\"");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "position": {
                    "x": 50,
                    "y": 75
                }
            }
            """;
        
        TableDTO table = objectMapper.readValue(json, TableDTO.class);
        
        assertThat(table).isNotNull();
        assertThat(table.getPosition()).isNotNull();
        assertThat(table.getPosition().getX()).isEqualTo(50);
        assertThat(table.getPosition().getY()).isEqualTo(75);
    }
}
