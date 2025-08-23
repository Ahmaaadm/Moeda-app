package com.accounting.util;

import javafx.scene.control.Alert;

public class AlertUtil {
    
    public static void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "نجح", message);
    }
    
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "خطأ", message);
    }
    
    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "تحذير", message);
    }
    
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}