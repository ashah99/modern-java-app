package com.developer.java;

import com.developer.java.model.Contact;
import com.developer.java.repository.ContactRepository;
import com.developer.java.ui.MainApp;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainLauncher extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // We assign the result of the builder to our static springContext field
        this.springContext = new SpringApplicationBuilder(MainLauncher.class)
                .web(org.springframework.boot.WebApplicationType.SERVLET)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        MainApp mainApp = new MainApp();

        // Connect the Repository from Spring to the UI
        ContactRepository repo = springContext.getBean(ContactRepository.class);
        mainApp.setRepository(repo);

        mainApp.start(primaryStage);
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        Application.launch(MainLauncher.class, args);
    }
    @Bean
    public CommandLineRunner seedData(ContactRepository repo) {
        return args -> {
            if (repo.count() == 0) { // Only add if the DB is empty
                repo.save(new Contact("John Doe", "john@example.com"));
                repo.save(new Contact("Jane Smith", "jane@example.com"));
                System.out.println("Database seeded with initial entries.");
            }
        };
    }
}