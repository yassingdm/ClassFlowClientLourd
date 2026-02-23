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

@DisplayName("Tests pour GroupeDTO")
class GroupeDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Devrait créer un groupe avec constructeur par défaut")
    void shouldCreateGroupeWithDefaultConstructor() {
        GroupeDTO groupe = new GroupeDTO();
        
        assertThat(groupe).isNotNull();
        assertThat(groupe.getEleves()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait créer un groupe avec ID et liste d'élèves")
    void shouldCreateGroupeWithIdAndEleves() {
        List<EleveDTO> eleves = new ArrayList<>();
        eleves.add(new EleveDTO("Dupont", "Jean"));
        eleves.add(new EleveDTO("Martin", "Marie"));
        
        GroupeDTO groupe = new GroupeDTO(1L, eleves);
        
        assertThat(groupe.getId()).isEqualTo(1L);
        assertThat(groupe.getEleves()).hasSize(2);
    }

    @Test
    @DisplayName("Devrait ajouter un élève au groupe")
    void shouldAddEleveToGroupe() {
        GroupeDTO groupe = new GroupeDTO();
        EleveDTO eleve = new EleveDTO("Dupont", "Jean");
        
        groupe.addEleve(eleve);
        
        assertThat(groupe.getEleves()).hasSize(1);
        assertThat(groupe.getEleves().get(0)).isEqualTo(eleve);
    }

    @Test
    @DisplayName("Devrait retirer un élève du groupe")
    void shouldRemoveEleveFromGroupe() {
        EleveDTO eleve1 = new EleveDTO("Dupont", "Jean");
        EleveDTO eleve2 = new EleveDTO("Martin", "Marie");
        
        List<EleveDTO> eleves = new ArrayList<>();
        eleves.add(eleve1);
        eleves.add(eleve2);
        
        GroupeDTO groupe = new GroupeDTO(1L, eleves);
        groupe.removeEleve(eleve1);
        
        assertThat(groupe.getEleves()).hasSize(1);
        assertThat(groupe.getEleves()).containsOnly(eleve2);
    }

    @Test
    @DisplayName("Devrait sérialiser en JSON")
    void shouldSerializeToJson() throws IOException {
        List<EleveDTO> eleves = new ArrayList<>();
        eleves.add(new EleveDTO("Dupont", "Jean"));
        
        GroupeDTO groupe = new GroupeDTO(10L, eleves);
        
        String json = objectMapper.writeValueAsString(groupe);
        
        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"eleves\"");
    }

    @Test
    @DisplayName("Devrait désérialiser depuis JSON")
    void shouldDeserializeFromJson() throws IOException {
        String json = """
            {
                "id": 5,
                "nom": "Groupe A",
                "eleves": [
                    {
                        "id": 1,
                        "nom": "Dupont",
                        "prenom": "Jean",
                        "remarques": []
                    }
                ]
            }
            """;
        
        GroupeDTO groupe = objectMapper.readValue(json, GroupeDTO.class);
        
        assertThat(groupe).isNotNull();
        assertThat(groupe.getId()).isEqualTo(5L);
        assertThat(groupe.getEleves()).hasSize(1);
        assertThat(groupe.getEleves().get(0).getNom()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("Devrait définir et récupérer le nom")
    void shouldSetAndGetNom() {
        GroupeDTO groupe = new GroupeDTO();
        groupe.setNom("Groupe Test");
        
        assertThat(groupe.getNom()).isEqualTo("Groupe Test");
    }
}
