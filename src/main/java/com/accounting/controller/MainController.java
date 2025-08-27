package com.accounting.controller;

import com.accounting.util.CurrencyManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML private BorderPane mainPane;
    @FXML private VBox sideMenu;
    @FXML private Button dataEntryBtn;
    @FXML private Button viewDataBtn;
    @FXML private Button employeesBtn;
    @FXML private Button settingsBtn;
    @FXML private Label currencyLabel;
    
    private Button activeButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateCurrencyDisplay();
        loadDataEntryPage();
        setActiveButton(dataEntryBtn);
    }
    
    @FXML
    private void showDataEntry() {
        loadDataEntryPage();
        setActiveButton(dataEntryBtn);
    }
    
    @FXML
    private void showViewData() {
        loadViewDataPage();
        setActiveButton(viewDataBtn);
    }
    
    @FXML
    private void showEmployees() {
        loadEmployeesPage();
        setActiveButton(employeesBtn);
    }
    
    @FXML
    private void showSettings() {
        loadSettingsPage();
        setActiveButton(settingsBtn);
    }
    
    private void loadDataEntryPage() {
        loadPage("/fxml/data-entry.fxml");
    }
    
    private void loadViewDataPage() {
        loadPage("/fxml/view-data.fxml");
    }
    
    private void loadEmployeesPage() {
        loadPage("/fxml/employees.fxml");
    }
    
    private void loadSettingsPage() {
        loadPage("/fxml/settings.fxml");
    }
    
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            mainPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        button.getStyleClass().add("active");
        activeButton = button;
    }
    
    public void updateCurrencyDisplay() {
        CurrencyManager cm = CurrencyManager.getInstance();
        currencyLabel.setText(cm.getCurrentCurrency() + " (1 Kz = " + cm.getExchangeRate() + " USD)");
    }
}