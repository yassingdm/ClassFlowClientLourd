package com.eidd.clientlourd;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import com.eidd.clientlourd.view.ClassRoomContainerView;
import com.eidd.clientlourd.view.LoginView;
import com.eidd.clientlourd.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Application principale ClassFlow Client Lourd
 */
public class ClassFlowClientApplication extends Application {
    
    private Stage primaryStage;
    private ClassFlowApiService apiService;
    private Scene loginScene;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        String backendUrl = detectAvailableBackend();
        System.out.println("Backend sélectionné : " + backendUrl);
        apiService = new ClassFlowApiService(backendUrl);

        LoginView loginView = new LoginView(apiService);
        loginView.setOnLoginSuccess(this::showMainView);
        loginScene = new Scene(loginView, 600, 400);

        primaryStage.setTitle("ClassFlow - Client Lourd");
        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    private void showMainView() {
        MainView mainView = new MainView(apiService);
        mainView.setOnClassRoomSelected(this::showClassRoomDetail);
        mainScene = new Scene(mainView, 800, 600);
        
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
    }

    private void showClassRoomDetail(ClassRoomDTO classRoom) {
        ClassRoomContainerView containerView = new ClassRoomContainerView(apiService, classRoom);
        containerView.setOnBack(this::showMainView);
        Scene detailScene = new Scene(containerView, 1200, 700);
        
        primaryStage.setScene(detailScene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
    }

    /**
     * Détecte automatiquement quel backend est disponible en testant d'abord
     * l'URL configurée dans application.properties, puis le serveur local en fallback
     * 
     * @return L'URL du backend disponible
     */
    private String detectAvailableBackend() {
        String configuredUrl = loadBackendUrlFromProperties();
        String localUrl = "http://localhost:8080";
        
        // Priorité 1 : URL configurée dans application.properties
        if (configuredUrl != null && isBackendAvailable(configuredUrl)) {
            System.out.println("✓ Backend distant accessible : " + configuredUrl);
            return configuredUrl;
        }
        
        // Priorité 2 : Backend local
        if (isBackendAvailable(localUrl)) {
            System.out.println("⚠ Backend distant inaccessible, utilisation du backend local : " + localUrl);
            return localUrl;
        }
        
        // Fallback : retourner l'URL configurée même si inaccessible
        // L'utilisateur verra l'erreur lors de la connexion
        System.err.println("✗ Aucun backend accessible. Tentative avec : " + (configuredUrl != null ? configuredUrl : localUrl));
        return configuredUrl != null ? configuredUrl : localUrl;
    }

    /**
     * Charge l'URL du backend depuis le fichier application.properties
     * 
     * @return L'URL configurée ou null si non trouvée
     */
    private String loadBackendUrlFromProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Fichier application.properties non trouvé");
                return null;
            }
            props.load(input);
            String url = props.getProperty("classflow.backend.url");
            if (url != null) {
                // Nettoyer l'URL (enlever le / final si présent)
                url = url.trim().replaceAll("/$", "");
            }
            return url;
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de application.properties : " + e.getMessage());
            return null;
        }
    }

    /**
     * Teste si un backend est accessible en envoyant une requête HEAD
     * 
     * @param baseUrl L'URL de base du backend à tester
     * @return true si le backend répond, false sinon
     */
    private boolean isBackendAvailable(String baseUrl) {
        // Tester d'abord /actuator/health puis /ping comme fallback
        String[] testEndpoints = {"/actuator/health", "/ping"};
        
        for (String endpoint : testEndpoints) {
            try {
                @SuppressWarnings("deprecation")
                URL url = new URL(baseUrl + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(2000); // 2 secondes de timeout
                connection.setReadTimeout(2000);
                
                int responseCode = connection.getResponseCode();
                connection.disconnect();
                
                // Accepter les codes 2xx et 3xx
                if (responseCode >= 200 && responseCode < 400) {
                    return true;
                }
            } catch (IOException e) {
                // Continuer avec l'endpoint suivant
            }
        }
        
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
