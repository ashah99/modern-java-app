package com.developer.java.ui;

import com.developer.java.model.Contact;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter Name");

        Button saveBtn = new Button("Save to Database");

        // We will connect the database logic here in the next step
       // saveBtn.setOnAction(e -> System.out.println("Saving: " + nameInput.getText()));
// Inside MainApp.java - update the start method:

        saveBtn.setOnAction(e -> {
            String name = nameInput.getText();
            if (!name.isEmpty()) {
                // 1. Create the data object
                Contact newContact = new Contact();
                newContact.setName(name);
                newContact.setEmail("test@example.com"); // Hardcoded for now

                // 2. Save it (We will pass the repository in the next step)
                System.out.println("Saved " + name + " to the H2 Database!");
                nameInput.clear();
            }
        });

        VBox layout = new VBox(10, nameInput, saveBtn);
        Scene scene = new Scene(layout, 300, 200);

        primaryStage.setTitle("JavaFX + JPA App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
