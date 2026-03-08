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
        // Header moderne
        VBox header = new VBox(10);
        header.setPadding(new Insets(25, 20, 25, 20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #e67e22, #ec8936); " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🎲 Roulette des Élèves");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label(classRoom.getNom());
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setStyle("-fx-text-fill: #fff8dc;");

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Zone d'affichage centrale
        VBox centerBox = new VBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(60, 50, 60, 50));
        centerBox.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #ecf0f1);");

        // Label d'affichage du nom - grande et professionnelle
        displayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 56));
        displayLabel.setTextFill(Color.web("#2c3e50"));
        displayLabel.setStyle("-fx-border-color: #e67e22; -fx-border-width: 4; " +
                              "-fx-border-radius: 12; -fx-background-radius: 12; " +
                              "-fx-padding: 40; -fx-min-width: 500; -fx-alignment: center; " +
                              "-fx-background-color: white; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        // Bouton de lancement modernisé
        spinButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        spinButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #e67e22, #ec8936); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 18 50; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 14;"
        );
        spinButton.setOnMouseEntered(e -> spinButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #d56e12, #dc7926); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 18 50; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 14;"
        ));
        spinButton.setOnMouseExited(e -> spinButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #e67e22, #ec8936); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 18 50; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 14;"
        ));
        spinButton.setOnAction(e -> spin());

        centerBox.getChildren().addAll(displayLabel, spinButton);

        // Compteur d'élèves stylisé
        Label countLabel = new Label("👥 Élèves disponibles: " + 
            (classRoom.getEleves() != null ? classRoom.getEleves().size() : 0));
        countLabel.setFont(Font.font("Segoe UI", 14));
        countLabel.setPadding(new Insets(15));
        countLabel.setAlignment(Pos.CENTER);
        countLabel.setMaxWidth(Double.MAX_VALUE);
        countLabel.setStyle(
            "-fx-background-color: #34495e; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold;"
        );

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
