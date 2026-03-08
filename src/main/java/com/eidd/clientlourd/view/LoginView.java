package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.UserDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {
    private final ClassFlowApiService apiService;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label messageLabel;
    private Runnable onLoginSuccess;

    public LoginView(ClassFlowApiService apiService) {
        this.apiService = apiService;
        setupUI();
    }

    private void setupUI() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        setAlignment(Pos.CENTER);
        setSpacing(0);
        setPadding(new Insets(0));

        // Spacer top
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        // Container principal
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: white; -fx-border-radius: 10; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);");
        mainContainer.setPadding(new Insets(40));
        mainContainer.setPrefWidth(450);
        mainContainer.setPrefHeight(500);

        // Titre
        Label titleLabel = new Label("ClassFlow");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        titleLabel.setAlignment(Pos.CENTER);

        // Sous-titre
        Label subtitleLabel = new Label("Gestion de Classe");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        subtitleLabel.setAlignment(Pos.CENTER);

        // Séparateur
        Label separatorLabel = new Label("━━━━━━━━━━━━━━━━");
        separatorLabel.setStyle("-fx-text-fill: #bdc3c7;");
        separatorLabel.setAlignment(Pos.CENTER);

        // Formulaire
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.TOP_CENTER);
        formGrid.setHgap(15);
        formGrid.setVgap(20);
        formGrid.setPadding(new Insets(10, 0, 10, 0));

        Label usernameLabel = new Label("Nom d'utilisateur");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12;");
        usernameField = new TextField();
        usernameField.setPromptText("demo1");
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-padding: 12; -fx-font-size: 13; -fx-border-color: #ecf0f1; " +
                              "-fx-border-width: 2; -fx-border-radius: 5; -fx-control-inner-background: #f8f9fa;");

        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12;");
        passwordField = new PasswordField();
        passwordField.setPromptText("demo1");
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-padding: 12; -fx-font-size: 13; -fx-border-color: #ecf0f1; " +
                              "-fx-border-width: 2; -fx-border-radius: 5; -fx-control-inner-background: #f8f9fa;");

        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 0, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 0, 3);
        GridPane.setHgrow(formGrid, Priority.ALWAYS);

        // Bouton login
        loginButton = new Button("SE CONNECTER");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, #2980b9, #3498db); " +
                            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; " +
                            "-fx-background-radius: 5; -fx-cursor: hand;");
        loginButton.setDefaultButton(true);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #3498db, #5dade2); " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; " +
            "-fx-background-radius: 5; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #2980b9, #3498db); " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; " +
            "-fx-background-radius: 5; -fx-cursor: hand;"));
        loginButton.setOnAction(e -> handleLogin());

        // Message
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12;");
        messageLabel.setWrapText(true);

        // Hint label
        Label hintLabel = new Label("Compte de test: demo1 / demo1");
        hintLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11; -fx-font-style: italic;");
        hintLabel.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(
            titleLabel, subtitleLabel, separatorLabel,
            formGrid, loginButton, messageLabel, hintLabel
        );

        // Spacer bottom
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        getChildren().addAll(topSpacer, mainContainer, bottomSpacer);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠️ Veuillez remplir tous les champs");
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("CONNEXION EN COURS...");
        messageLabel.setText("");

        new Thread(() -> {
            try {
                apiService.authenticate(username, password);
                UserDTO user = apiService.getCurrentUser();

                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("✓ Connexion réussie ! Bienvenue " + user.getUsername());
                    messageLabel.setStyle("-fx-text-fill: #27ae60;");

                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                if (onLoginSuccess != null) {
                                    onLoginSuccess.run();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("✗ Erreur: " + e.getMessage());
                    messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                    loginButton.setDisable(false);
                    loginButton.setText("SE CONNECTER");
                });
            }
        }).start();
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }
}
