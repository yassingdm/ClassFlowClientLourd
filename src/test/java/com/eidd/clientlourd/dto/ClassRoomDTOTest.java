package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour ClassRoomDTO")
class ClassRoomDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer une ClassRoom avec un constructeur par défaut")
    void shouldCreateClassRoomWithDefaultConstructor() {
        ClassRoomDTO classRoom = new ClassRoomDTO();
        
        assertThat(classRoom).isNotNull();
        assertThat(classRoom.getEleves()).isNotNull().isEmpty();
        assertThat(classRoom.getTables()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait créer une ClassRoom avec un nom")
    void shouldCreateClassRoomWithName() {
        ClassRoomDTO classRoom = new ClassRoomDTO("Classe 1");
        
        assertThat(classRoom.getNom()).isEqualTo("Classe 1");
        assertThat(classRoom.getEleves()).isNotNull().isEmpty();
        assertThat(classRoom.getTables()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait définir et récupérer l'ID")
    void shouldSetAndGetId() {
        ClassRoomDTO classRoom = new ClassRoomDTO();
        classRoom.setId(42L);
        
        assertThat(classRoom.getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("Devrait ajouter des élèves")
    void shouldAddEleves() {
        ClassRoomDTO classRoom = new ClassRoomDTO("Classe 1");
        List<EleveDTO> eleves = new ArrayList<>();
        eleves.add(new EleveDTO("Dupont", "Jean"));
        eleves.add(new EleveDTO("Martin", "Marie"));
        classRoom.setEleves(eleves);
        
        assertThat(classRoom.getEleves()).hasSize(2);
        assertThat(classRoom.getEleves().get(0).getNom()).isEqualTo("Dupont");
        assertThat(classRoom.getEleves().get(1).getNom()).isEqualTo("Martin");
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        ClassRoomDTO classRoom = new ClassRoomDTO("Classe Test");
        classRoom.setId(1L);
        
        String json = objectMapper.writeValueAsString(classRoom);
        
        assertThat(json).contains("\"nom\":\"Classe Test\"");
        assertThat(json).contains("\"id\":1");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "id": 1,
                "nom": "Classe Test",
                "eleves": [],
                "tables": []
            }
            """;
        
        ClassRoomDTO classRoom = objectMapper.readValue(json, ClassRoomDTO.class);
        
        assertThat(classRoom).isNotNull();
        assertThat(classRoom.getId()).isEqualTo(1L);
        assertThat(classRoom.getNom()).isEqualTo("Classe Test");
        assertThat(classRoom.getEleves()).isEmpty();
        assertThat(classRoom.getTables()).isEmpty();
    }

    @Test
    @DisplayName("Devrait ignorer les propriétés inconnues lors de la désérialisation")
    void shouldIgnoreUnknownProperties() throws IOException {
        String json = """
            {
                "id": 1,
                "nom": "Classe Test",
                "eleves": [],
                "tables": [],
                "unknownProperty": "value"
            }
            """;
        
        assertThatCode(() -> objectMapper.readValue(json, ClassRoomDTO.class))
            .doesNotThrowAnyException();
    }
}
