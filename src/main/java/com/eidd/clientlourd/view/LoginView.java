package com.eidd.clientlourd.view;

import com.eidd.clientlourd.dto.UserDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));

        Label titleLabel = new Label("ClassFlow - Client Lourd");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label subtitleLabel = new Label("Connexion");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        Label usernameLabel = new Label("Nom d'utilisateur:");
        usernameField = new TextField();
        usernameField.setPromptText("demo1");
        usernameField.setPrefWidth(200);

        Label passwordLabel = new Label("Mot de passe:");
        passwordField = new PasswordField();
        passwordField.setPromptText("demo1");
        passwordField.setPrefWidth(200);

        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        loginButton = new Button("Se connecter");
        loginButton.setPrefWidth(150);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        Label hintLabel = new Label("Compte de test: demo1 / demo1");
        hintLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        getChildren().addAll(titleLabel, subtitleLabel, formGrid, loginButton, messageLabel, hintLabel);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        loginButton.setDisable(true);
        messageLabel.setText("Connexion en cours...");
        messageLabel.setStyle("-fx-text-fill: blue;");

        new Thread(() -> {
            try {
                apiService.authenticate(username, password);
                UserDTO user = apiService.getCurrentUser();

                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("Connexion réussie ! Bienvenue " + user.getUsername());
                    messageLabel.setStyle("-fx-text-fill: green;");
                    
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
                    messageLabel.setText("Erreur: " + e.getMessage());
                    messageLabel.setStyle("-fx-text-fill: red;");
                    loginButton.setDisable(false);
                });
            }
        }).start();
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }
}
