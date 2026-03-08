package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Vue conteneur pour toutes les fonctionnalités d'une classe
 * Intègre : Élèves, Roulette, Groupes
 */
public class ClassRoomContainerView extends BorderPane {
    private final ClassFlowApiService apiService;
    private final ClassRoomDTO classRoom;
    private Runnable onBack;

    public ClassRoomContainerView(ClassFlowApiService apiService, ClassRoomDTO classRoom) {
        this.apiService = apiService;
        this.classRoom = classRoom;
        setupUI();
    }

    private void setupUI() {
        // Header moderne
        HBox header = new HBox(15);
        header.setPadding(new Insets(20, 20, 20, 20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e); " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Retour");
        backButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; " +
                           "-fx-font-size: 12; -fx-background-radius: 5;");
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: #3498db; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; " +
            "-fx-font-size: 12; -fx-background-radius: 5;"));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: #2980b9; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; " +
            "-fx-font-size: 12; -fx-background-radius: 5;"));
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Label titleLabel = new Label(classRoom.getNom());
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Spacer pour pousser les éléments
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(backButton, titleLabel, spacer);

        // TabPane moderne
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 13;");

        // Tab 1: Élèves et remarques
        Tab studentsTab = new Tab("👥 Élèves");
        studentsTab.setStyle("-fx-padding: 10;");
        ClassRoomDetailView detailView = new ClassRoomDetailView(apiService, classRoom);
        detailView.setOnBack(null);
        studentsTab.setContent(detailView);

        // Tab 2: Roulette
        Tab rouletteTab = new Tab("🎲 Roulette");
        rouletteTab.setStyle("-fx-padding: 10;");
        RandomStudentView rouletteView = new RandomStudentView(classRoom);
        rouletteTab.setContent(rouletteView);

        // Tab 3: Groupes
        Tab groupsTab = new Tab("👨‍👩‍👧‍👦 Groupes");
        groupsTab.setStyle("-fx-padding: 10;");
        GroupManagementView groupsView = new GroupManagementView(classRoom, apiService);
        groupsTab.setContent(groupsView);

        tabPane.getTabs().addAll(studentsTab, rouletteTab, groupsTab);

        setTop(header);
        setCenter(tabPane);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
}
