package com.eidd.clientlourd.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour EleveDTO")
class EleveDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer un élève avec constructeur par défaut")
    void shouldCreateEleveWithDefaultConstructor() {
        EleveDTO eleve = new EleveDTO();
        
        assertThat(eleve).isNotNull();
        assertThat(eleve.getRemarques()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait créer un élève avec nom et prénom")
    void shouldCreateEleveWithNomAndPrenom() {
        EleveDTO eleve = new EleveDTO("Dupont", "Jean");
        
        assertThat(eleve.getNom()).isEqualTo("Dupont");
        assertThat(eleve.getPrenom()).isEqualTo("Jean");
        assertThat(eleve.getRemarques()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait définir et récupérer toutes les propriétés")
    void shouldSetAndGetAllProperties() {
        EleveDTO eleve = new EleveDTO();
        eleve.setId(1L);
        eleve.setNom("Martin");
        eleve.setPrenom("Marie");
        eleve.setRemarques(new ArrayList<>());
        
        assertThat(eleve.getId()).isEqualTo(1L);
        assertThat(eleve.getNom()).isEqualTo("Martin");
        assertThat(eleve.getPrenom()).isEqualTo("Marie");
        assertThat(eleve.getRemarques()).isEmpty();
    }

    @Test
    @DisplayName("Devrait retourner le format prénom + nom avec toString()")
    void shouldReturnFormattedStringWithToString() {
        EleveDTO eleve = new EleveDTO("Dupont", "Jean");
        
        assertThat(eleve.toString()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        EleveDTO eleve = new EleveDTO("Dupont", "Jean");
        eleve.setId(123L);
        
        String json = objectMapper.writeValueAsString(eleve);
        
        assertThat(json).contains("\"nom\":\"Dupont\"");
        assertThat(json).contains("\"prenom\":\"Jean\"");
        assertThat(json).contains("\"id\":123");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "id": 456,
                "nom": "Martin",
                "prenom": "Marie",
                "remarques": []
            }
            """;
        
        EleveDTO eleve = objectMapper.readValue(json, EleveDTO.class);
        
        assertThat(eleve).isNotNull();
        assertThat(eleve.getId()).isEqualTo(456L);
        assertThat(eleve.getNom()).isEqualTo("Martin");
        assertThat(eleve.getPrenom()).isEqualTo("Marie");
        assertThat(eleve.getRemarques()).isEmpty();
    }

    @Test
    @DisplayName("Devrait ignorer les propriétés inconnues")
    void shouldIgnoreUnknownProperties() throws IOException {
        String json = """
            {
                "id": 1,
                "nom": "Dupont",
                "prenom": "Jean",
                "remarques": [],
                "unknownField": "value"
            }
            """;
        
        assertThatCode(() -> objectMapper.readValue(json, EleveDTO.class))
            .doesNotThrowAnyException();
    }
}
