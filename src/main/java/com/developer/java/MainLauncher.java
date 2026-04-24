package com.developer.java;

import com.developer.java.ui.MainApp;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainLauncher extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // This line actually starts the Spring web server and H2 console
        springContext = SpringApplication.run(MainLauncher.class);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) {
        MainApp mainApp = new MainApp();
        mainApp.start(primaryStage);
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        Application.launch(MainLauncher.class, args);
    }
}
