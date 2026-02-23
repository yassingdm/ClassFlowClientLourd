package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.dto.EleveDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Vue de la roulette pour tirer un élève au hasard
 */
public class RandomStudentView extends BorderPane {
    private final ClassRoomDTO classRoom;
    private final Label displayLabel;
    private final Button spinButton;
    private final Random random = new Random();
    private Timeline animation;
    private boolean isSpinning = false;

    public RandomStudentView(ClassRoomDTO classRoom) {
        this.classRoom = classRoom;
        this.displayLabel = new Label("?");
        this.spinButton = new Button("🎲 Lancer la roulette");
        
        setupUI();
    }

    private void setupUI() {
        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #FF5722;");
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Roulette des élèves");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label(classRoom.getNom());
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: white;");

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Zone d'affichage centrale
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        centerBox.setStyle("-fx-background-color: white;");

        // Label d'affichage du nom
        displayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        displayLabel.setTextFill(Color.web("#333"));
        displayLabel.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; " +
                              "-fx-border-radius: 8; -fx-background-radius: 8; " +
                              "-fx-padding: 25; -fx-min-width: 400; -fx-alignment: center;");

        // Bouton de lancement
        spinButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        spinButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; " +
                           "-fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");
        spinButton.setOnAction(e -> spin());

        centerBox.getChildren().addAll(displayLabel, spinButton);

        // Compteur d'élèves
        Label countLabel = new Label("Élèves disponibles: " + 
            (classRoom.getEleves() != null ? classRoom.getEleves().size() : 0));
        countLabel.setFont(Font.font("Arial", 14));
        countLabel.setPadding(new Insets(10));
        countLabel.setAlignment(Pos.CENTER);
        countLabel.setMaxWidth(Double.MAX_VALUE);
        countLabel.setStyle("-fx-background-color: #f5f5f5;");

        setTop(header);
        setCenter(centerBox);
        setBottom(countLabel);
    }

    private void spin() {
        if (isSpinning) return;

        List<EleveDTO> eleves = classRoom.getEleves();
        if (eleves == null || eleves.isEmpty()) {
            displayLabel.setText("Aucun élève");
            return;
        }

        isSpinning = true;
        spinButton.setDisable(true);

        // Animation simple de défilement
        final int[] currentIndex = {0};
        final int totalSpins = 15; // 15 spins rapides
        final int[] spinCount = {0};

        animation = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            // Afficher un élève aléatoire
            currentIndex[0] = random.nextInt(eleves.size());
            EleveDTO randomEleve = eleves.get(currentIndex[0]);
            displayLabel.setText(randomEleve.getPrenom() + " " + randomEleve.getNom());

            spinCount[0]++;

            if (spinCount[0] >= totalSpins) {
                animation.stop();
                finishSpin(randomEleve);
            }
        }));

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void finishSpin(EleveDTO winner) {
        isSpinning = false;
        spinButton.setDisable(false);

        // Afficher le gagnant
        displayLabel.setText(winner.getPrenom() + " " + winner.getNom());
    }
}
