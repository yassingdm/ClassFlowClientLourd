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
        // Header avec bouton retour
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #4CAF50;");
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Retour");
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: #4CAF50; " +
                           "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Label titleLabel = new Label(classRoom.getNom());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(backButton, titleLabel);

        // TabPane avec toutes les vues
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Élèves et remarques
        Tab studentsTab = new Tab("👥 Élèves");
        ClassRoomDetailView detailView = new ClassRoomDetailView(apiService, classRoom);
        detailView.setOnBack(null); // Le bouton de retour est dans le header principal
        studentsTab.setContent(detailView);

        // Tab 2: Roulette
        Tab rouletteTab = new Tab("🎲 Roulette");
        RandomStudentView rouletteView = new RandomStudentView(classRoom);
        rouletteTab.setContent(rouletteView);

        // Tab 3: Groupes
        Tab groupsTab = new Tab("👨‍👩‍👧‍👦 Groupes");
        GroupManagementView groupsView = new GroupManagementView(classRoom, apiService);
        groupsTab.setContent(groupsView);

        // Tab 4: Plan de classe
        Tab planTab = new Tab("🪑 Plan de classe");
        ClassRoomPlanView planView = new ClassRoomPlanView(apiService, classRoom.getId());
        planTab.setContent(planView);

        tabPane.getTabs().addAll(studentsTab, rouletteTab, groupsTab, planTab);

        setTop(header);
        setCenter(tabPane);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
}
