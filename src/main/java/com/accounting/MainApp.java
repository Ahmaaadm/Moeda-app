package com.accounting;

import com.accounting.database.DatabaseManager;
import com.accounting.util.CurrencyManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;

public class MainApp extends Application {

    private static long appStartTime;

    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.currentTimeMillis();
        long stepStart;

        System.out.println("=== JAVAFX START() METHOD ENTERED ===");
        System.out.println("Time since main(): " + (startTime - appStartTime) + "ms");

        // Step 1: Set locale
        stepStart = System.currentTimeMillis();
        System.out.println("[1] Setting Arabic locale...");
        Locale.setDefault(new Locale("ar", "SA"));
        System.out.println("[1] Locale set in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 2: Initialize database
        stepStart = System.currentTimeMillis();
        System.out.println("[2] Initializing database...");
        DatabaseManager.getInstance().initializeDatabase();
        System.out.println("[2] Database initialized in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 3: Initialize currency manager
        stepStart = System.currentTimeMillis();
        System.out.println("[3] Loading currency settings...");
        CurrencyManager.getInstance().loadSettings();
        System.out.println("[3] Currency settings loaded in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 4: Load FXML
        stepStart = System.currentTimeMillis();
        System.out.println("[4] Loading FXML file...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        System.out.println("[4] FXML loader created in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 5: Create scene
        stepStart = System.currentTimeMillis();
        System.out.println("[5] Loading FXML and creating scene...");
        Scene scene = new Scene(loader.load(), 1200, 800);
        System.out.println("[5] Scene created in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 6: Load CSS
        stepStart = System.currentTimeMillis();
        System.out.println("[6] Loading CSS styles...");
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        System.out.println("[6] CSS loaded in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 7: Configure stage
        stepStart = System.currentTimeMillis();
        System.out.println("[7] Configuring primary stage...");
        primaryStage.setTitle("نظام المحاسبة - Accounting System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setMaximized(true);
        System.out.println("[7] Stage configured in: " + (System.currentTimeMillis() - stepStart) + "ms");

        // Step 8: Show stage
        stepStart = System.currentTimeMillis();
        System.out.println("[8] Showing primary stage...");
        primaryStage.show();
        System.out.println("[8] Stage shown in: " + (System.currentTimeMillis() - stepStart) + "ms");

        long totalStartupTime = System.currentTimeMillis() - appStartTime;
        long fxStartupTime = System.currentTimeMillis() - startTime;

        System.out.println("=== STARTUP COMPLETE ===");
        System.out.println("JavaFX start() method took: " + fxStartupTime + "ms");
        System.out.println("Total application startup: " + totalStartupTime + "ms");
        System.out.println("========================");
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application shutting down...");
        long shutdownStart = System.currentTimeMillis();

        DatabaseManager.getInstance().closeConnection();
        super.stop();

        System.out.println("Shutdown completed in: " + (System.currentTimeMillis() - shutdownStart) + "ms");
    }

    public static void main(String[] args) {
        appStartTime = System.currentTimeMillis();
        System.out.println("=== APPLICATION MAIN() STARTED ===");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("JavaFX version: " + System.getProperty("javafx.version"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
        System.out.println("===================================");

        long launchStart = System.currentTimeMillis();
        launch(args);

        System.out.println("Application.launch() returned after: " +
                (System.currentTimeMillis() - launchStart) + "ms");
    }
}