package com.eidd.clientlourd.service;

import com.eidd.clientlourd.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class ClassFlowApiService {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authHeader;

    public ClassFlowApiService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Authentifie l'utilisateur avec username et password (Basic Auth)
     */
    public void authenticate(String username, String password) {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        this.authHeader = "Basic " + encodedAuth;
    }

    /**
     * Récupère les informations de l'utilisateur connecté
     */
    public UserDTO getCurrentUser() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/me"))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), UserDTO.class);
        } else {
            throw new IOException("Authentication failed: " + response.statusCode());
        }
    }

    /**
     * Récupère toutes les classes
     */
    public List<ClassRoomDTO> getAllClassRooms() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms"))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(response.body(), 
                    typeFactory.constructCollectionType(List.class, ClassRoomDTO.class));
        } else {
            throw new IOException("Failed to fetch classrooms: " + response.statusCode());
        }
    }

    /**
     * Récupère une classe par son ID
     */
    public ClassRoomDTO getClassRoom(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + id))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ClassRoomDTO.class);
        } else {
            throw new IOException("Failed to fetch classroom: " + response.statusCode());
        }
    }

    /**
     * Crée une nouvelle classe
     */
    public ClassRoomDTO createClassRoom(ClassRoomDTO classRoom) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(classRoom);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), ClassRoomDTO.class);
        } else {
            throw new IOException("Failed to create classroom: " + response.statusCode());
        }
    }

    /**
     * Supprime une classe
     */
    public void deleteClassRoom(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + id))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to delete classroom: " + response.statusCode());
        }
    }

    /**
     * Ajoute un élève à une classe
     */
    public ClassRoomDTO addEleveToClassRoom(long classRoomId, EleveDTO eleve) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(eleve);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), ClassRoomDTO.class);
        } else {
            throw new IOException("Failed to add eleve: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Met à jour un élève
     */
    public ClassRoomDTO updateEleve(long classRoomId, long eleveId, EleveDTO eleve) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(eleve);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves/" + eleveId))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ClassRoomDTO.class);
        } else {
            throw new IOException("Failed to update eleve: " + response.statusCode());
        }
    }

    /**
     * Supprime un élève d'une classe
     */
    public void deleteEleve(long classRoomId, long eleveId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves/" + eleveId))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to delete eleve: " + response.statusCode());
        }
    }

    /**
     * Crée une nouvelle remarque
     */
    public RemarqueDTO createRemarque(RemarqueRequestDTO remarqueRequest) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(remarqueRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/remarques"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), RemarqueDTO.class);
        } else {
            throw new IOException("Failed to create remarque: " + response.statusCode());
        }
    }

    /**
     * Récupère toutes les remarques d'un élève d'une classe
     */
    public List<RemarqueDTO> getRemarquesForEleve(long classRoomId, long eleveId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves/" + eleveId + "/remarques"))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(response.body(), 
                    typeFactory.constructCollectionType(List.class, RemarqueDTO.class));
        } else {
            throw new IOException("Failed to fetch remarques: " + response.statusCode());
        }
    }

    /**
     * Supprime une remarque
     */
    public void deleteRemarque(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/remarques/" + id))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to delete remarque: " + response.statusCode());
        }
    }
}
