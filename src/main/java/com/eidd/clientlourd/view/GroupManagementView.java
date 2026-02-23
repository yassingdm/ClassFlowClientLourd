package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.dto.EleveDTO;
import com.eidd.clientlourd.dto.GroupeDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vue de gestion des groupes avec CRUD complet
 */
public class GroupManagementView extends BorderPane {
    private static final DataFormat ELEVE_FORMAT = new DataFormat("application/x-eleve");
    
    private final ClassRoomDTO classRoom;
    private final ClassFlowApiService apiService;
    private final VBox groupsContainer;
    private final ListView<EleveDTO> availableStudentsList;
    private final Label statusLabel;
    private List<GroupeDTO> groupes = new ArrayList<>();
    private Map<Long, EleveDTO> allStudentsMap = new HashMap<>();

    public GroupManagementView(ClassRoomDTO classRoom, ClassFlowApiService apiService) {
        this.classRoom = classRoom;
        this.apiService = apiService;
        this.groupsContainer = new VBox(15);
        this.availableStudentsList = new ListView<>();
        this.statusLabel = new Label();
        
        initializeStudentsMap();
        setupUI();
        loadGroupes();
    }

    private void initializeStudentsMap() {
        if (classRoom.getEleves() != null) {
            for (EleveDTO eleve : classRoom.getEleves()) {
                allStudentsMap.put(eleve.getId(), eleve);
            }
        }
    }

    private void setupUI() {
        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2196F3;");
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Gestion des groupes");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label(classRoom.getNom());
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #f5f5f5;");

        Button createRandomButton = new Button("➕ Groupes aléatoires");
        createRandomButton.setOnAction(e -> createRandomGroups());

        Button createManualButton = new Button("➕ Nouveau groupe");
        createManualButton.setOnAction(e -> createManualGroup());

        Button refreshButton = new Button("🔄 Rafraîchir");
        refreshButton.setOnAction(e -> loadGroupes());

        toolbar.getChildren().addAll(createRandomButton, createManualButton, refreshButton);

        // Main content: split between groups and available students
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPositions(0.7);

        // Left: Groups display
        ScrollPane groupsScroll = new ScrollPane(groupsContainer);
        groupsScroll.setFitToWidth(true);
        groupsScroll.setStyle("-fx-background-color: white;");
        groupsContainer.setPadding(new Insets(20));

        // Right: Available students
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #fafafa;");

        Label availableLabel = new Label("Élèves disponibles");
        availableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        availableStudentsList.setCellFactory(lv -> createDraggableStudentCell());
        availableStudentsList.setStyle("-fx-background-color: white;");

        rightPanel.getChildren().addAll(availableLabel, availableStudentsList);
        VBox.setVgrow(availableStudentsList, Priority.ALWAYS);

        mainContent.getItems().addAll(groupsScroll, rightPanel);

        // Status bar
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        statusLabel.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #666;");

        setTop(new VBox(header, toolbar));
        setCenter(mainContent);
        setBottom(statusLabel);
    }

    private ListCell<EleveDTO> createDraggableStudentCell() {
        ListCell<EleveDTO> cell = new ListCell<>() {
            @Override
            protected void updateItem(EleveDTO eleve, boolean empty) {
                super.updateItem(eleve, empty);
                if (empty || eleve == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(eleve.getPrenom() + " " + eleve.getNom());
                }
            }
        };

        cell.setOnDragDetected(event -> {
            EleveDTO eleve = cell.getItem();
            if (eleve != null) {
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.put(ELEVE_FORMAT, eleve.getId());
                content.putString(eleve.getPrenom() + " " + eleve.getNom());
                db.setContent(content);
                event.consume();
            }
        });

        return cell;
    }

    private void loadGroupes() {
        new Thread(() -> {
            try {
                List<GroupeDTO> loadedGroupes = apiService.getGroupes(classRoom.getId());
                Platform.runLater(() -> {
                    this.groupes = loadedGroupes;
                    updateUI();
                    setStatus("Groupes chargés");
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
            }
        }).start();
    }

    private void updateUI() {
        groupsContainer.getChildren().clear();

        // Élèves déjà dans des groupes
        Set<Long> studentsInGroups = new HashSet<>();
        for (GroupeDTO groupe : groupes) {
            if (groupe.getEleves() != null) {
                studentsInGroups.addAll(groupe.getEleves().stream()
                    .map(EleveDTO::getId).collect(Collectors.toSet()));
            }
        }

        // Afficher les groupes
        for (int i = 0; i < groupes.size(); i++) {
            GroupeDTO groupe = groupes.get(i);
            VBox groupBox = createGroupBox(groupe, i + 1);
            groupsContainer.getChildren().add(groupBox);
        }

        if (groupes.isEmpty()) {
            Label emptyLabel = new Label("Aucun groupe créé");
            emptyLabel.setFont(Font.font("Arial", 14));
            emptyLabel.setTextFill(Color.GRAY);
            groupsContainer.getChildren().add(emptyLabel);
        }

        // Mettre à jour la liste des élèves disponibles
        availableStudentsList.getItems().clear();
        if (classRoom.getEleves() != null) {
            for (EleveDTO eleve : classRoom.getEleves()) {
                if (!studentsInGroups.contains(eleve.getId())) {
                    availableStudentsList.getItems().add(eleve);
                }
            }
        }
    }

    private VBox createGroupBox(GroupeDTO groupe, int groupNumber) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; " +
                     "-fx-border-radius: 8; -fx-background-color: white; " +
                     "-fx-background-radius: 8;");

        // Header du groupe
        HBox groupHeader = new HBox(10);
        groupHeader.setAlignment(Pos.CENTER_LEFT);

        // Nom du groupe (éditable)
        String displayName = (groupe.getNom() != null && !groupe.getNom().isEmpty()) 
            ? groupe.getNom() 
            : "Groupe " + groupNumber;
        Label groupLabel = new Label(displayName);
        groupLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        groupLabel.setTextFill(Color.web("#2196F3"));
        groupLabel.setStyle("-fx-cursor: hand; -fx-padding: 5;");
        
        // Bouton d'édition du nom
        Button editNameButton = new Button("✏️");
        editNameButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; -fx-cursor: hand; -fx-font-size: 12;");
        editNameButton.setOnAction(e -> editGroupName(groupe, groupLabel));
        
        // Double-clic pour éditer aussi
        groupLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editGroupName(groupe, groupLabel);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("🗑");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
        deleteButton.setOnAction(e -> deleteGroupe(groupe));

        groupHeader.getChildren().addAll(groupLabel, editNameButton, spacer, deleteButton);

        // Liste des élèves dans le groupe
        FlowPane studentsFlow = new FlowPane(10, 10);
        studentsFlow.setPadding(new Insets(10, 0, 0, 0));

        if (groupe.getEleves() != null) {
            for (EleveDTO eleve : groupe.getEleves()) {
                HBox studentBox = new HBox(5);
                studentBox.setPadding(new Insets(5, 10, 5, 10));
                studentBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 15; -fx-cursor: hand;");
                studentBox.setAlignment(Pos.CENTER);

                Label studentLabel = new Label(eleve.getPrenom() + " " + eleve.getNom());
                
                Button removeButton = new Button("✖");
                removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #f44336; " +
                                     "-fx-font-size: 10; -fx-cursor: hand; -fx-padding: 0 5;");
                removeButton.setOnAction(e -> removeStudentFromGroup(groupe, eleve));

                studentBox.getChildren().addAll(studentLabel, removeButton);
                studentsFlow.getChildren().add(studentBox);
            }
        }

        // Drop zone pour ajouter des élèves
        Label dropLabel = new Label("⬇ Glissez des élèves ici");
        dropLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
        dropLabel.setPadding(new Insets(10));

        studentsFlow.setOnDragOver(event -> {
            if (event.getGestureSource() != studentsFlow &&
                event.getDragboard().hasContent(ELEVE_FORMAT)) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        studentsFlow.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(ELEVE_FORMAT)) {
                Long eleveId = (Long) db.getContent(ELEVE_FORMAT);
                addStudentToGroup(groupe, eleveId);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        box.getChildren().addAll(groupHeader, studentsFlow, dropLabel);
        return box;
    }

    private void editGroupName(GroupeDTO groupe, Label groupLabel) {
        TextInputDialog dialog = new TextInputDialog(groupe.getNom() != null ? groupe.getNom() : "");
        dialog.setTitle("Éditer le nom du groupe");
        dialog.setHeaderText("Modifier le nom du groupe");
        dialog.setContentText("Nom:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName != null && !newName.trim().isEmpty()) {
                new Thread(() -> {
                    try {
                        apiService.updateGroupe(classRoom.getId(), groupe.getId(), 
                            List.of(), List.of(), newName.trim());
                        Platform.runLater(() -> {
                            groupe.setNom(newName.trim());
                            groupLabel.setText(newName.trim());
                            setStatus("Nom du groupe mis à jour");
                        });
                    } catch (IOException | InterruptedException e) {
                        Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void createRandomGroups() {
        TextInputDialog dialog = new TextInputDialog("4");
        dialog.setTitle("Groupes aléatoires");
        dialog.setHeaderText("Création de groupes aléatoires");
        dialog.setContentText("Nombre de groupes:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int groupCount = Integer.parseInt(input);
                if (groupCount < 1 || groupCount > 20) {
                    setStatus("Erreur: nombre de groupes invalide (1-20)");
                    return;
                }

                new Thread(() -> {
                    try {
                        List<GroupeDTO> newGroupes = apiService.createGroupesAleatoires(
                            classRoom.getId(), groupCount);
                        Platform.runLater(() -> {
                            groupes = newGroupes;
                            updateUI();
                            setStatus("Groupes aléatoires créés: " + groupCount);
                        });
                    } catch (IOException | InterruptedException e) {
                        Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
                    }
                }).start();
            } catch (NumberFormatException e) {
                setStatus("Erreur: nombre invalide");
            }
        });
    }

    private void createManualGroup() {
        // Demander le nom du nouveau groupe
        TextInputDialog nameDialog = new TextInputDialog("Groupe " + (groupes.size() + 1));
        nameDialog.setTitle("Nouveau groupe");
        nameDialog.setHeaderText("Créer un nouveau groupe");
        nameDialog.setContentText("Nom du groupe:");

        nameDialog.showAndWait().ifPresent(groupName -> {
            new Thread(() -> {
                try {
                    // Créer un groupe vide avec un nom
                    List<List<Long>> groupesList = new ArrayList<>();
                    groupesList.add(new ArrayList<>()); // Un groupe vide
                    
                    List<String> noms = new ArrayList<>();
                    noms.add(groupName.trim());
                    
                    apiService.createGroupes(classRoom.getId(), groupesList, noms);
                    Platform.runLater(() -> {
                        loadGroupes(); // Recharger tous les groupes
                        setStatus("Nouveau groupe créé: " + groupName.trim());
                    });
                } catch (IOException | InterruptedException e) {
                    Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
                }
            }).start();
        });
    }

    private void addStudentToGroup(GroupeDTO groupe, Long eleveId) {
        new Thread(() -> {
            try {
                apiService.updateGroupe(classRoom.getId(), groupe.getId(), 
                    List.of(eleveId), List.of(), null);
                Platform.runLater(() -> {
                    loadGroupes();
                    setStatus("Élève ajouté au groupe");
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
            }
        }).start();
    }

    private void removeStudentFromGroup(GroupeDTO groupe, EleveDTO eleve) {
        new Thread(() -> {
            try {
                apiService.updateGroupe(classRoom.getId(), groupe.getId(), 
                    List.of(), List.of(eleve.getId()), null);
                Platform.runLater(() -> {
                    loadGroupes();
                    setStatus("Élève retiré du groupe");
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
            }
        }).start();
    }

    private void deleteGroupe(GroupeDTO groupe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer le groupe?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        apiService.deleteGroupe(classRoom.getId(), groupe.getId());
                        Platform.runLater(() -> {
                            loadGroupes();
                            setStatus("Groupe supprimé");
                        });
                    } catch (IOException | InterruptedException e) {
                        Platform.runLater(() -> setStatus("Erreur: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
