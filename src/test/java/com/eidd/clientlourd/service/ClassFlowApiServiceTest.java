package com.eidd.clientlourd.service;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.dto.EleveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests pour ClassFlowApiService")
class ClassFlowApiServiceTest {

    private ClassFlowApiService apiService;
    private static final String BASE_URL = "http://localhost:8080/api";

    @BeforeEach
    void setUp() {
        apiService = new ClassFlowApiService(BASE_URL);
    }

    @Test
    @DisplayName("Devrait créer une instance avec une URL de base")
    void shouldCreateInstanceWithBaseUrl() {
        assertThat(apiService).isNotNull();
    }

    @Test
    @DisplayName("Devrait accepter les identifiants d'authentification")
    void shouldAcceptAuthenticationCredentials() {
        assertThatCode(() -> apiService.authenticate("demo1", "demo1"))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Devrait créer une nouvelle classe (objet DTO)")
    void shouldCreateNewClassRoomDTO() {
        ClassRoomDTO classRoom = new ClassRoomDTO("Test Classe");
        
        assertThat(classRoom).isNotNull();
        assertThat(classRoom.getNom()).isEqualTo("Test Classe");
    }

    @Test
    @DisplayName("Devrait créer un nouvel élève (objet DTO)")
    void shouldCreateNewEleveDTO() {
        EleveDTO eleve = new EleveDTO("Dupont", "Jean");
        
        assertThat(eleve).isNotNull();
        assertThat(eleve.getNom()).isEqualTo("Dupont");
        assertThat(eleve.getPrenom()).isEqualTo("Jean");
        assertThat(eleve.toString()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("Devrait gérer plusieurs authentifications successives")
    void shouldHandleMultipleAuthentications() {
        assertThatCode(() -> {
            apiService.authenticate("user1", "password1");
            apiService.authenticate("user2", "password2");
            apiService.authenticate("user3", "password3");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Devrait accepter une URL de base vide")
    void shouldAcceptEmptyBaseUrl() {
        ClassFlowApiService service = new ClassFlowApiService("");
        
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("Devrait accepter une URL de base avec port personnalisé")
    void shouldAcceptCustomPortInBaseUrl() {
        ClassFlowApiService service = new ClassFlowApiService("http://localhost:9090/api");
        
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("Devrait gérer l'authentification avec des caractères spéciaux")
    void shouldHandleAuthenticationWithSpecialCharacters() {
        assertThatCode(() -> apiService.authenticate("user@domain.com", "p@ssw0rd!"))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Devrait gérer l'authentification avec des espaces")
    void shouldHandleAuthenticationWithSpaces() {
        assertThatCode(() -> apiService.authenticate("user name", "pass word"))
            .doesNotThrowAnyException();
    }
}
