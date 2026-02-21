package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.*;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class ClassRoomDetailView extends BorderPane {
    private final ClassFlowApiService apiService;
    private ClassRoomDTO classRoom;
    private ListView<EleveDTO> eleveListView;
    private ObservableList<EleveDTO> eleves;
    private TextArea remarquesArea;
    private ListView<RemarqueDTO> remarquesListView;
    private Label statusLabel;
    private Runnable onBack;

    public ClassRoomDetailView(ClassFlowApiService apiService, ClassRoomDTO classRoom) {
        this.apiService = apiService;
        this.classRoom = classRoom;
        setupUI();
        refreshClassRoom();
    }

    private void setupUI() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #4CAF50;");

        HBox headerTop = new HBox(10);
        Button backButton = new Button("← Retour");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Label titleLabel = new Label(classRoom.getNom());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        headerTop.getChildren().addAll(backButton, titleLabel);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(headerTop, statusLabel);

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10));

        Label eleveTitle = new Label("Élèves");
        eleveTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        eleves = FXCollections.observableArrayList();
        eleveListView = new ListView<>(eleves);
        eleveListView.setCellFactory(param -> new EleveListCell());
        eleveListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadRemarquesForEleve(newVal);
            }
        });

        // Boutons gestion élèves
        HBox eleveButtons = new HBox(10);
        Button addEleveButton = new Button("Ajouter élève");
        addEleveButton.setOnAction(e -> addEleve());

        Button editEleveButton = new Button("Modifier");
        editEleveButton.setOnAction(e -> editEleve());

        Button deleteEleveButton = new Button("Supprimer");
        deleteEleveButton.setOnAction(e -> deleteEleve());

        eleveButtons.getChildren().addAll(addEleveButton, editEleveButton, deleteEleveButton);

        centerBox.getChildren().addAll(eleveTitle, eleveListView, eleveButtons);

        // Droite: remarques
        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10));
        rightBox.setPrefWidth(300);

        Label remarqueTitle = new Label("Remarques de l'élève");
        remarqueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        remarquesListView = new ListView<>();
        remarquesListView.setPrefHeight(300);
        remarquesListView.setCellFactory(param -> new ListCell<RemarqueDTO>() {
            @Override
            protected void updateItem(RemarqueDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("• " + item.getIntitule());
                }
            }
        });

        HBox remarqueButtonBox = new HBox(10);
        Button addRemarqueButton = new Button("Ajouter une remarque");
        addRemarqueButton.setOnAction(e -> addRemarque());
        
        Button deleteRemarqueButton = new Button("Supprimer");
        deleteRemarqueButton.setOnAction(e -> deleteRemarque());

        remarqueButtonBox.getChildren().addAll(addRemarqueButton, deleteRemarqueButton);

        rightBox.getChildren().addAll(remarqueTitle, remarquesListView, remarqueButtonBox);

        setTop(header);
        setCenter(centerBox);
        setRight(rightBox);
    }

    private void refreshClassRoom() {
        statusLabel.setText("Chargement...");
        new Thread(() -> {
            try {
                classRoom = apiService.getClassRoom(classRoom.getId());
                Platform.runLater(() -> {
                    eleves.clear();
                    if (classRoom.getEleves() != null) {
                        eleves.addAll(classRoom.getEleves());
                    }
                    statusLabel.setText(eleves.size() + " élève(s)");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Erreur: " + e.getMessage());
                    showError("Erreur", e.getMessage());
                });
            }
        }).start();
    }

    private void addEleve() {
        Dialog<EleveDTO> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un élève");
        dialog.setHeaderText("Informations de l'élève");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nomField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new EleveDTO(nomField.getText(), prenomField.getText());
            }
            return null;
        });

        Optional<EleveDTO> result = dialog.showAndWait();
        result.ifPresent(eleve -> {
            new Thread(() -> {
                try {
                    apiService.addEleveToClassRoom(classRoom.getId(), eleve);
                    Platform.runLater(this::refreshClassRoom);
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur", e.getMessage()));
                }
            }).start();
        });
    }

    private void editEleve() {
        EleveDTO selected = eleveListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner un élève");
            return;
        }

        Dialog<EleveDTO> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'élève");
        dialog.setHeaderText("Informations de l'élève");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                EleveDTO updated = new EleveDTO(nomField.getText(), prenomField.getText());
                updated.setId(selected.getId());
                return updated;
            }
            return null;
        });

        Optional<EleveDTO> result = dialog.showAndWait();
        result.ifPresent(eleve -> {
            new Thread(() -> {
                try {
                    apiService.updateEleve(classRoom.getId(), selected.getId(), eleve);
                    Platform.runLater(this::refreshClassRoom);
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur", e.getMessage()));
                }
            }).start();
        });
    }

    private void deleteEleve() {
        EleveDTO selected = eleveListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner un élève");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'élève " + selected.getPrenom() + " " + selected.getNom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.deleteEleve(classRoom.getId(), selected.getId());
                    Platform.runLater(this::refreshClassRoom);
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur", e.getMessage()));
                }
            }).start();
        }
    }

    private void loadRemarquesForEleve(EleveDTO eleve) {
        new Thread(() -> {
            try {
                var remarques = apiService.getRemarquesForEleve(classRoom.getId(), eleve.getId());
                Platform.runLater(() -> {
                    remarquesListView.getItems().clear();
                    remarquesListView.getItems().addAll(remarques);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "Impossible de charger les remarques: " + e.getMessage());
                });
            }
        }).start();
    }

    private void deleteRemarque() {
        RemarqueDTO selected = remarquesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner une remarque à supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cette remarque ?");
        confirm.setContentText("\"" + selected.getIntitule() + "\"\n\nCette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.deleteRemarque(selected.getId());
                    EleveDTO currentEleve = eleveListView.getSelectionModel().getSelectedItem();
                    if (currentEleve != null) {
                        Platform.runLater(() -> loadRemarquesForEleve(currentEleve));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur", e.getMessage()));
                }
            }).start();
        }
    }

    private void addRemarque() {
        EleveDTO selected = eleveListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner un élève");
            return;
        }

        Dialog<RemarqueRequestDTO> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une remarque");
        dialog.setHeaderText("Remarque pour " + selected.getPrenom() + " " + selected.getNom());

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<RemarqueType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(RemarqueType.values());
        typeComboBox.setValue(RemarqueType.REMARQUE_GENERALE);

        TextField intituleField = new TextField();
        intituleField.setPromptText("Texte de la remarque (optionnel pour types prédéfinis)");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Remarque:"), 0, 1);
        grid.add(intituleField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String intitule = intituleField.getText().trim();

                if (intitule.isEmpty()) {
                    intitule = typeComboBox.getValue().toString();
                }
                return new RemarqueRequestDTO(
                        intitule,
                        selected.getId(),
                        classRoom.getId(),
                        typeComboBox.getValue()
                );
            }
            return null;
        });

        Optional<RemarqueRequestDTO> result = dialog.showAndWait();
        result.ifPresent(remarqueRequest -> {
            new Thread(() -> {
                try {
                    apiService.createRemarque(remarqueRequest);
                    Platform.runLater(() -> loadRemarquesForEleve(selected));
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Erreur", e.getMessage()));
                }
            }).start();
        });
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
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

    // Cell personnalisée pour afficher les élèves
    private static class EleveListCell extends ListCell<EleveDTO> {
        @Override
        protected void updateItem(EleveDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getPrenom() + " " + item.getNom());
                setStyle("-fx-font-size: 14px; -fx-padding: 8px;");
            }
        }
    }
}
