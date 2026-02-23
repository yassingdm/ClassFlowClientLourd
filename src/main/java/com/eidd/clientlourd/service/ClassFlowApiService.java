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

    /**
     * Crée une nouvelle table dans une classe
     */
    public TableDTO createTable(long classRoomId, TableCreateRequest tableRequest) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(tableRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/tables"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), TableDTO.class);
        } else {
            throw new IOException("Failed to create table: " + response.statusCode());
        }
    }

    /**
     * Supprime une table d'une classe
     */
    public void deleteTable(long classRoomId, int tableIndex) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/tables/" + tableIndex))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to delete table: " + response.statusCode());
        }
    }

    /**
     * Crée des groupes aléatoires dans une classe
     */
    public List<GroupeDTO> createGroupesAleatoires(long classRoomId, int groupCount) throws IOException, InterruptedException {
        GroupeRandomCreateRequest request = new GroupeRandomCreateRequest(groupCount);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/groupes/aleatoire"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(response.body(), 
                    typeFactory.constructCollectionType(List.class, GroupeDTO.class));
        } else {
            throw new IOException("Failed to create random groups: " + response.statusCode());
        }
    }

    /**
     * Crée des groupes manuellement dans une classe
     */
    public List<GroupeDTO> createGroupes(long classRoomId, List<List<Long>> groupes, List<String> noms) throws IOException, InterruptedException {
        GroupeCreateRequest request = new GroupeCreateRequest();
        request.setGroupes(groupes);
        request.setNoms(noms);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/groupes"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(response.body(), 
                    typeFactory.constructCollectionType(List.class, GroupeDTO.class));
        } else {
            throw new IOException("Failed to create groups: " + response.statusCode());
        }
    }

    /**
     * Récupère tous les groupes d'une classe
     */
    public List<GroupeDTO> getGroupes(long classRoomId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/groupes"))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(response.body(), 
                    typeFactory.constructCollectionType(List.class, GroupeDTO.class));
        } else {
            throw new IOException("Failed to fetch groups: " + response.statusCode());
        }
    }

    /**
     * Met à jour un groupe
     */
    public GroupeDTO updateGroupe(long classRoomId, long groupeId, List<Long> addEleveIds, List<Long> removeEleveIds, String nom) throws IOException, InterruptedException {
        GroupeUpdateRequest request = new GroupeUpdateRequest();
        request.setAddEleveIds(addEleveIds);
        request.setRemoveEleveIds(removeEleveIds);
        request.setNom(nom);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/groupes/" + groupeId))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), GroupeDTO.class);
        } else {
            throw new IOException("Failed to update group: " + response.statusCode());
        }
    }

    /**
     * Supprime un groupe
     */
    public void deleteGroupe(long classRoomId, long groupeId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/groupes/" + groupeId))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to delete group: " + response.statusCode());
        }
    }

    /**
     * Récupère le plan de classe avec les élèves assignés aux tables
     */
    public ClassRoomPlanDTO getClassRoomPlan(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + id + "/plan"))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ClassRoomPlanDTO.class);
        } else {
            throw new IOException("Failed to fetch classroom plan: " + response.statusCode());
        }
    }

    /**
     * Met à jour la position d'une table
     */
    public TableDTO updateTablePosition(long classRoomId, int tableIndex, int x, int y) throws IOException, InterruptedException {
        TablePositionUpdateRequest request = new TablePositionUpdateRequest(x, y);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/tables/" + tableIndex + "/position"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), TableDTO.class);
        } else {
            throw new IOException("Failed to update table position: " + response.statusCode());
        }
    }

    /**
     * Assigne un élève à une table
     */
    public EleveDTO assignEleveToTable(long classRoomId, long eleveId, int tableIndex) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves/" + eleveId + "/table/" + tableIndex))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), EleveDTO.class);
        } else {
            throw new IOException("Failed to assign eleve to table: " + response.statusCode());
        }
    }

    /**
     * Échange deux élèves de place
     */
    public void swapEleves(long classRoomId, long eleveId1, long eleveId2) throws IOException, InterruptedException {
        EleveSwapRequest request = new EleveSwapRequest(eleveId1, eleveId2);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/classrooms/" + classRoomId + "/eleves/swap"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to swap eleves: " + response.statusCode());
        }
    }
}
