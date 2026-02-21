package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MainView extends BorderPane {
    private final ClassFlowApiService apiService;
    private ListView<ClassRoomDTO> classRoomListView;
    private ObservableList<ClassRoomDTO> classRooms;
    private Consumer<ClassRoomDTO> onClassRoomSelected;
    private Label statusLabel;

    public MainView(ClassFlowApiService apiService) {
        this.apiService = apiService;
        setupUI();
        loadClassRooms();
    }

    private void setupUI() {

        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2196F3;");

        Label titleLabel = new Label("Mes Classes");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        statusLabel = new Label("Chargement...");
        statusLabel.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(titleLabel, statusLabel);

        // Liste des classes
        classRooms = FXCollections.observableArrayList();
        classRoomListView = new ListView<>(classRooms);
        classRoomListView.setCellFactory(param -> new ClassRoomListCell());
        classRoomListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ClassRoomDTO selected = classRoomListView.getSelectionModel().getSelectedItem();
                if (selected != null && onClassRoomSelected != null) {
                    onClassRoomSelected.accept(selected);
                }
            }
        });

        // Boutons en bas
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setStyle("-fx-background-color: #f0f0f0;");

        Button refreshButton = new Button("Actualiser");
        refreshButton.setOnAction(e -> loadClassRooms());

        Button newClassButton = new Button("Nouvelle classe");
        newClassButton.setOnAction(e -> createNewClassRoom());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> deleteSelectedClassRoom());

        Button openButton = new Button("Ouvrir");
        openButton.setOnAction(e -> {
            ClassRoomDTO selected = classRoomListView.getSelectionModel().getSelectedItem();
            if (selected != null && onClassRoomSelected != null) {
                onClassRoomSelected.accept(selected);
            }
        });

        buttonBar.getChildren().addAll(refreshButton, newClassButton, deleteButton, openButton);

        // Layout
        setTop(header);
        setCenter(classRoomListView);
        setBottom(buttonBar);
    }

    private void loadClassRooms() {
        statusLabel.setText("Chargement des classes...");
        new Thread(() -> {
            try {
                List<ClassRoomDTO> rooms = apiService.getAllClassRooms();
                Platform.runLater(() -> {
                    classRooms.clear();
                    classRooms.addAll(rooms);
                    statusLabel.setText(rooms.size() + " classe(s) chargée(s)");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Erreur: " + e.getMessage());
                    showError("Erreur de chargement", e.getMessage());
                });
            }
        }).start();
    }

    private void createNewClassRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle classe");
        dialog.setHeaderText("Créer une nouvelle classe");
        dialog.setContentText("Nom de la classe:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                new Thread(() -> {
                    try {
                        ClassRoomDTO newClass = new ClassRoomDTO(name.trim());
                        apiService.createClassRoom(newClass);
                        Platform.runLater(this::loadClassRooms);
                    } catch (Exception e) {
                        Platform.runLater(() -> showError("Erreur de création", e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void deleteSelectedClassRoom() {
        ClassRoomDTO selected = classRoomListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner une classe à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la classe \"" + selected.getNom() + "\" ?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.deleteClassRoom(selected.getId());
                    Platform.runLater(this::loadClassRooms);
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur de suppression", e.getMessage()));
                }
            }).start();
        }
    }

    public void setOnClassRoomSelected(Consumer<ClassRoomDTO> onClassRoomSelected) {
        this.onClassRoomSelected = onClassRoomSelected;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Cell personnalisée pour afficher les classes
    private static class ClassRoomListCell extends ListCell<ClassRoomDTO> {
        @Override
        protected void updateItem(ClassRoomDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                int eleveCount = (item.getEleves() != null) 
                        ? item.getEleves().size() : 0;
                setText(item.getNom() + " - " + eleveCount + " élève(s)");
                setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
            }
        }
    }
}
