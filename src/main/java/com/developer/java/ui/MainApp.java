package com.developer.java.ui;

import com.developer.java.model.Contact;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    // ObservableList is the "Live" data source for the UI
    private final ObservableList<Contact> contactData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        // 1. Create the Table
        TableView<Contact> table = new TableView<>();

        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(nameCol, emailCol);
        table.setItems(contactData);

        // 2. Input Fields
        TextField nameInput = new TextField();
        nameInput.setPromptText("Name");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Email");

        Button saveBtn = new Button("Add Contact");
        saveBtn.setOnAction(e -> {
            Contact c = new Contact(nameInput.getText(), emailInput.getText());
            contactData.add(c); // This updates the UI immediately
            // Note: In the next lesson, we call repository.save(c) here
            nameInput.clear();
            emailInput.clear();
        });

        // 3. Layout
        VBox layout = new VBox(10, table, nameInput, emailInput, saveBtn);
        Scene scene = new Scene(layout, 400, 400);

        primaryStage.setTitle("Contact Manager v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}