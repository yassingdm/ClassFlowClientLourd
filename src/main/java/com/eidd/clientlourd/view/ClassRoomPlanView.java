package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.*;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

/**
 * Vue du plan de classe interactif avec drag-and-drop
 */
public class ClassRoomPlanView extends BorderPane {
    private static final int GRID_WIDTH = 8;
    private static final int GRID_HEIGHT = 6;
    private static final int CELL_SIZE = 80;

    private final ClassFlowApiService apiService;
    private ClassRoomPlanDTO classRoomPlan;
    private final Pane planPane;
    private final Label statusLabel;

    // Map pour garder trace des vues et indices
    private final Map<Integer, StackPane> tableViews = new HashMap<>();
    private final Map<Long, StackPane> eleveViews = new HashMap<>();
    private final Map<Long, Integer> eleveTableIndex = new HashMap<>();

    // Pour l'échange d'élèves
    private EleveDTO selectedEleve1 = null;
    private StackPane selectedEleveView1 = null;

    public ClassRoomPlanView(ClassFlowApiService apiService, long classRoomId) {
        this.apiService = apiService;

        // Titre
        Label titleLabel = new Label("Plan de Classe");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");

        // Barre d'outils
        HBox toolbar = new HBox(10);
        toolbar.setStyle("-fx-padding: 10px; -fx-background-color: #2c3e50;");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button addTableBtn = new Button("➕ Ajouter Table");
        addTableBtn.setOnAction(e -> addTable());
        addTableBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Button refreshBtn = new Button("🔄 Rafraîchir");
        refreshBtn.setOnAction(e -> refreshClassRoom());
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        toolbar.getChildren().addAll(addTableBtn, refreshBtn);

        // Status bar
        statusLabel = new Label("Chargement...");
        statusLabel.setStyle("-fx-padding: 5px; -fx-background-color: #34495e; -fx-text-fill: white;");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        // Zone de plan
        planPane = new Pane();
        planPane.setPrefSize(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        planPane.setStyle("-fx-background-color: #ecf0f1;");

        ScrollPane scrollPane = new ScrollPane(planPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox topBox = new VBox(titleLabel, toolbar);
        setTop(topBox);
        setCenter(scrollPane);
        setBottom(statusLabel);

        // Charger le plan initial
        loadClassRoomPlan(classRoomId);
    }

    private void loadClassRoomPlan(long classRoomId) {
        new Thread(() -> {
            try {
                classRoomPlan = apiService.getClassRoomPlan(classRoomId);
                Platform.runLater(this::loadPlan);
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur lors du chargement du plan: " + e.getMessage()));
            }
        }).start();
    }

    private void loadPlan() {
        planPane.getChildren().clear();
        tableViews.clear();
        eleveViews.clear();
        eleveTableIndex.clear();

        // Dessiner la grille
        drawGrid();

        if (classRoomPlan == null || classRoomPlan.getTables() == null) {
            statusLabel.setText("Aucune table trouvée");
            return;
        }

        // Charger les tables avec leurs élèves
        for (int i = 0; i < classRoomPlan.getTables().size(); i++) {
            TablePlanDTO tablePlan = classRoomPlan.getTables().get(i);
            final int tableIndex = i;

            // Créer la vue de la table
            StackPane tableView = createTableView(tablePlan, tableIndex);
            tableViews.put(tableIndex, tableView);
            planPane.getChildren().add(tableView);

            // Si un élève est assigné à cette table, créer sa vue
            if (tablePlan.getEleve() != null) {
                EleveDTO eleve = tablePlan.getEleve();
                StackPane eleveView = createEleveView(eleve, tablePlan.getX(), tablePlan.getY());
                eleveViews.put(eleve.getId(), eleveView);
                eleveTableIndex.put(eleve.getId(), tableIndex);
                planPane.getChildren().add(eleveView);
            }
        }

        statusLabel.setText("Cliquez sur 2 élèves ou 2 tables occupées pour les échanger. Faites glisser les tables pour les déplacer.");
    }

    private void drawGrid() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.LIGHTGRAY);
                cell.setStrokeWidth(0.5);
                planPane.getChildren().add(cell);
            }
        }
    }

    private StackPane createTableView(TablePlanDTO tablePlan, int tableIndex) {
        StackPane tablePane = new StackPane();

        // Position
        tablePane.setLayoutX(tablePlan.getX() * CELL_SIZE + 5);
        tablePane.setLayoutY(tablePlan.getY() * CELL_SIZE + 5);

        Rectangle rect = new Rectangle(CELL_SIZE - 10, CELL_SIZE - 10);
        rect.setFill(Color.web("#8B4513"));
        rect.setStroke(Color.web("#654321"));
        rect.setStrokeWidth(2);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        VBox tableContent = new VBox(2);
        tableContent.setAlignment(Pos.CENTER);
        tableContent.setMaxWidth(CELL_SIZE - 18);

        if (tablePlan.getEleve() != null) {
            Text eleveNameText = new Text(tablePlan.getEleve().getPrenom() + "\n" + tablePlan.getEleve().getNom());
            eleveNameText.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            eleveNameText.setFill(Color.WHITE);
            eleveNameText.setTextAlignment(TextAlignment.CENTER);
            eleveNameText.setWrappingWidth(CELL_SIZE - 20);
            tableContent.getChildren().add(eleveNameText);
        } else {
            Text emptyText = new Text("Table libre");
            emptyText.setFont(Font.font("Arial", FontWeight.NORMAL, 9));
            emptyText.setFill(Color.web("#E0E0E0"));
            emptyText.setTextAlignment(TextAlignment.CENTER);
            tableContent.getChildren().add(emptyText);
        }

        tablePane.getChildren().addAll(rect, tableContent);
        tablePane.setCursor(Cursor.HAND);

        // Drag and drop de la table
        final double[] dragDelta = new double[2];
        tablePane.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                dragDelta[0] = tablePane.getLayoutX() - event.getSceneX();
                dragDelta[1] = tablePane.getLayoutY() - event.getSceneY();
                tablePane.toFront();
                
                // Mettre l'élève au premier plan aussi
                if (tablePlan.getEleve() != null) {
                    StackPane eleveView = eleveViews.get(tablePlan.getEleve().getId());
                    if (eleveView != null) {
                        eleveView.toFront();
                    }
                }
            }
        });

        tablePane.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double newX = event.getSceneX() + dragDelta[0];
                double newY = event.getSceneY() + dragDelta[1];

                // Snap to grid
                int gridX = (int) Math.round(newX / CELL_SIZE);
                int gridY = (int) Math.round(newY / CELL_SIZE);

                gridX = Math.max(0, Math.min(GRID_WIDTH - 1, gridX));
                gridY = Math.max(0, Math.min(GRID_HEIGHT - 1, gridY));

                tablePane.setLayoutX(gridX * CELL_SIZE + 5);
                tablePane.setLayoutY(gridY * CELL_SIZE + 5);

                // Déplacer l'élève avec la table
                if (tablePlan.getEleve() != null) {
                    StackPane eleveView = eleveViews.get(tablePlan.getEleve().getId());
                    if (eleveView != null) {
                        eleveView.setLayoutX(gridX * CELL_SIZE + CELL_SIZE - 24);
                        eleveView.setLayoutY(gridY * CELL_SIZE + 4);
                    }
                }
            }
        });

        tablePane.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                // Sauvegarder la nouvelle position
                int gridX = (int) ((tablePane.getLayoutX() - 5) / CELL_SIZE);
                int gridY = (int) ((tablePane.getLayoutY() - 5) / CELL_SIZE);
                
                // Vérifier si une autre table occupe déjà cette position
                if (isPositionOccupied(gridX, gridY, tableIndex)) {
                    showError("Une table existe déjà à cette position (X:" + gridX + ", Y:" + gridY + ")");
                    // Restaurer la position originale
                    refreshClassRoom();
                } else {
                    updateTablePosition(tableIndex, gridX, gridY);
                }
            }
        });

        tablePane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && tablePlan.getEleve() != null) {
                handleEleveSelectionForSwap(tablePlan.getEleve(), tablePane);
            }
        });

        tablePane.setOnDragOver(event -> {
            if (event.getGestureSource() != tablePane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        tablePane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                try {
                    long eleveId = Long.parseLong(dragboard.getString());
                    assignEleveToTable(eleveId, tableIndex);
                    success = true;
                } catch (NumberFormatException ignored) {
                    success = false;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Menu contextuel
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Supprimer table");
        deleteItem.setOnAction(e -> deleteTable(tableIndex));
        contextMenu.getItems().add(deleteItem);
        tablePane.setOnContextMenuRequested(e -> contextMenu.show(tablePane, e.getScreenX(), e.getScreenY()));

        return tablePane;
    }

    private StackPane createEleveView(EleveDTO eleve, int x, int y) {
        StackPane elevePane = new StackPane();

        elevePane.setLayoutX(x * CELL_SIZE + CELL_SIZE - 24);
        elevePane.setLayoutY(y * CELL_SIZE + 4);

        Rectangle bg = new Rectangle(20, 20);
        bg.setFill(Color.web("#4CAF50"));
        bg.setStroke(Color.web("#388E3C"));
        bg.setStrokeWidth(2);
        bg.setArcWidth(20);
        bg.setArcHeight(20);

        Text actionText = new Text("↕");
        actionText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        actionText.setFill(Color.WHITE);

        elevePane.getChildren().addAll(bg, actionText);
        elevePane.setCursor(Cursor.HAND);

        elevePane.setOnDragDetected(event -> {
            Dragboard dragboard = elevePane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(String.valueOf(eleve.getId()));
            dragboard.setContent(clipboardContent);
            event.consume();
        });

        // Clic pour échanger deux élèves
        elevePane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleEleveSelectionForSwap(eleve, elevePane);
            }
        });

        return elevePane;
    }

    private void handleEleveSelectionForSwap(EleveDTO eleve, StackPane sourcePane) {
        Rectangle sourceBg = (Rectangle) sourcePane.getChildren().get(0);

        if (selectedEleve1 == null) {
            selectedEleve1 = eleve;
            selectedEleveView1 = sourcePane;
            sourceBg.setStroke(Color.YELLOW);
            sourceBg.setStrokeWidth(3);
            statusLabel.setText("Sélectionnez un deuxième élève pour échanger");
            return;
        }

        if (selectedEleve1.getId() == eleve.getId()) {
            resetSelectionStyle(sourcePane);
            selectedEleve1 = null;
            selectedEleveView1 = null;
            statusLabel.setText("Cliquez sur 2 élèves ou 2 tables occupées pour les échanger");
            return;
        }

        swapEleves(selectedEleve1.getId(), eleve.getId());

        resetSelectionStyle(selectedEleveView1);
        selectedEleve1 = null;
        selectedEleveView1 = null;
    }

    private void resetSelectionStyle(StackPane pane) {
        Rectangle bg = (Rectangle) pane.getChildren().get(0);
        if (tableViews.containsValue(pane)) {
            bg.setStroke(Color.web("#654321"));
        } else {
            bg.setStroke(Color.web("#388E3C"));
        }
        bg.setStrokeWidth(2);
    }

    private void assignEleveToTable(long eleveId, int tableIndex) {
        new Thread(() -> {
            try {
                apiService.assignEleveToTable(classRoomPlan.getClassRoomId(), eleveId, tableIndex);
                Platform.runLater(() -> {
                    showStatus("Élève déplacé avec succès", false);
                    refreshClassRoom();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur lors du déplacement de l'élève: " + e.getMessage()));
            }
        }).start();
    }

    private void updateTablePosition(int tableIndex, int x, int y) {
        new Thread(() -> {
            try {
                apiService.updateTablePosition(classRoomPlan.getClassRoomId(), tableIndex, x, y);
                Platform.runLater(() -> {
                    showStatus("Position de la table mise à jour (X:" + x + ", Y:" + y + ")", false);
                    // Mettre à jour le plan local
                    if (tableIndex < classRoomPlan.getTables().size()) {
                        TablePlanDTO tablePlan = classRoomPlan.getTables().get(tableIndex);
                        tablePlan.setX(x);
                        tablePlan.setY(y);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur lors de la mise à jour de la position: " + e.getMessage());
                    // Rafraîchir pour restaurer la position originale
                    refreshClassRoom();
                });
            }
        }).start();
    }

    private void swapEleves(long eleveId1, long eleveId2) {
        new Thread(() -> {
            try {
                apiService.swapEleves(classRoomPlan.getClassRoomId(), eleveId1, eleveId2);
                Platform.runLater(() -> {
                    showStatus("Élèves échangés avec succès", false);
                    refreshClassRoom();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur lors de l'échange: " + e.getMessage()));
            }
        }).start();
    }

    private void addTable() {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Ajouter une table");
        dialog.setHeaderText("Position de la nouvelle table");
        dialog.setContentText("X (0-" + (GRID_WIDTH - 1) + "):");

        dialog.showAndWait().ifPresent(xStr -> {
            TextInputDialog dialogY = new TextInputDialog("0");
            dialogY.setTitle("Ajouter une table");
            dialogY.setHeaderText("Position de la nouvelle table");
            dialogY.setContentText("Y (0-" + (GRID_HEIGHT - 1) + "):");

            dialogY.showAndWait().ifPresent(yStr -> {
                try {
                    int x = Integer.parseInt(xStr);
                    int y = Integer.parseInt(yStr);

                    if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
                        showError("Position invalide");
                        return;
                    }

                    // Vérifier si une table existe déjà à cette position
                    if (isPositionOccupied(x, y, -1)) {
                        showError("Une table existe déjà à cette position (X:" + x + ", Y:" + y + ")");
                        return;
                    }

                    TableCreateRequest request = new TableCreateRequest(x, y);
                    new Thread(() -> {
                        try {
                            apiService.createTable(classRoomPlan.getClassRoomId(), request);
                            Platform.runLater(() -> {
                                showStatus("Table ajoutée", false);
                                refreshClassRoom();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> showError("Erreur lors de l'ajout de la table: " + e.getMessage()));
                        }
                    }).start();
                } catch (NumberFormatException e) {
                    showError("Coordonnées invalides");
                }
            });
        });
    }

    private void deleteTable(int tableIndex) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer table");
        confirm.setHeaderText("Supprimer cette table ?");
        confirm.setContentText("Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        apiService.deleteTable(classRoomPlan.getClassRoomId(), tableIndex);
                        Platform.runLater(() -> {
                            showStatus("Table supprimée", false);
                            refreshClassRoom();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> showError("Erreur: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void refreshClassRoom() {
        loadClassRoomPlan(classRoomPlan.getClassRoomId());
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "#ff5252" : "white") + "; -fx-padding: 5px; -fx-background-color: #34495e;");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Vérifie si une position est déjà occupée par une autre table
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @param excludedIndex Index de la table à exclure de la vérification (-1 pour aucune exclusion)
     * @return true si la position est occupée par une autre table
     */
    private boolean isPositionOccupied(int x, int y, int excludedIndex) {
        if (classRoomPlan == null || classRoomPlan.getTables() == null) {
            return false;
        }

        for (int i = 0; i < classRoomPlan.getTables().size(); i++) {
            if (i == excludedIndex) {
                continue;
            }

            TablePlanDTO table = classRoomPlan.getTables().get(i);
            if (table.getX() == x && table.getY() == y) {
                return true;
            }
        }

        return false;
    }
}
