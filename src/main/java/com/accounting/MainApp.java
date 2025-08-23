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
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set Arabic locale
        Locale.setDefault(new Locale("ar", "SA"));
        
        // Initialize database
        DatabaseManager.getInstance().initializeDatabase();
        
        // Initialize currency manager
        CurrencyManager.getInstance().loadSettings();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        primaryStage.setTitle("نظام المحاسبة - Accounting System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        DatabaseManager.getInstance().closeConnection();
        super.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}