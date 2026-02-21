package com.eidd.clientlourd;

import com.eidd.clientlourd.dto.ClassRoomDTO;
import com.eidd.clientlourd.service.ClassFlowApiService;
import com.eidd.clientlourd.view.ClassRoomDetailView;
import com.eidd.clientlourd.view.LoginView;
import com.eidd.clientlourd.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        
        String backendUrl = System.getProperty("backend.url", "http://localhost:8080");
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
        ClassRoomDetailView detailView = new ClassRoomDetailView(apiService, classRoom);
        detailView.setOnBack(this::showMainView);
        Scene detailScene = new Scene(detailView, 1000, 600);
        
        primaryStage.setScene(detailScene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
