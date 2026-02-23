package com.eidd.clientlourd;

import javafx.application.Application;

/**
 * Point d'entrée principal pour lancer l'application via java -jar
 * Cette classe wrapper est nécessaire pour JavaFX car on ne peut pas
 * lancer directement une classe qui hérite de Application avec java -jar
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(ClassFlowClientApplication.class, args);
    }
}
