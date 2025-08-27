package com.accounting.controller;

import com.accounting.util.AlertUtil;
import com.accounting.util.CurrencyManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    
    @FXML private RadioButton usdRadio;
    @FXML private RadioButton lbpRadio;
    @FXML private TextField exchangeRateField;
    @FXML private Button saveBtn;
    
    private ToggleGroup currencyGroup;
    private CurrencyManager currencyManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyManager = CurrencyManager.getInstance();
        setupCurrencyGroup();
        loadCurrentSettings();
        setupValidation();
    }
    
    private void setupCurrencyGroup() {
        currencyGroup = new ToggleGroup();
        usdRadio.setToggleGroup(currencyGroup);
        lbpRadio.setToggleGroup(currencyGroup);
        
        currencyGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            exchangeRateField.setDisable(newToggle == lbpRadio);
        });
    }
    
    private void setupValidation() {
        exchangeRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                exchangeRateField.setText(oldValue);
            }
        });
    }
    
    private void loadCurrentSettings() {
        String currency = currencyManager.getCurrentCurrency();
        if ("Kz".equals(currency)) {
            usdRadio.setSelected(true);
        } else {
            lbpRadio.setSelected(true);
        }
        
        exchangeRateField.setText(String.valueOf(currencyManager.getExchangeRate()));
        exchangeRateField.setDisable(lbpRadio.isSelected());
    }
    
    @FXML
    private void saveSettings() {
        String selectedCurrency = usdRadio.isSelected() ? "Kz" : "USD";
        
        double exchangeRate = 1.0;
        if (usdRadio.isSelected()) {
            try {
                exchangeRate = Double.parseDouble(exchangeRateField.getText());
                if (exchangeRate <= 0) {
                    AlertUtil.showWarning("يجب أن يكون سعر الصرف أكبر من الصفر");
                    return;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showWarning("يرجى إدخال سعر صرف صحيح");
                return;
            }
        }
        
        currencyManager.updateSettings(selectedCurrency, exchangeRate);
        AlertUtil.showSuccess("تم حفظ الإعدادات بنجاح");
        
        // Update main window currency display
        try {
            MainController mainController = (MainController) exchangeRateField.getScene().getWindow().getUserData();
            if (mainController != null) {
                mainController.updateCurrencyDisplay();
            }
        } catch (Exception e) {
            // Ignore if main controller is not accessible
        }
    }
}