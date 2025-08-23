package com.accounting.controller;

import com.accounting.database.DatabaseManager;
import com.accounting.model.Employee;
import com.accounting.model.Transaction;
import com.accounting.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class DataEntryController implements Initializable {
    
    @FXML private TabPane tabPane;
    
    // Sales Tab
    @FXML private ComboBox<Employee> salesEmployeeCombo;
    @FXML private TextField salesAmountField;
    @FXML private DatePicker salesDatePicker;
    @FXML private TextField salesTimeField;
    @FXML private TextArea salesNotesArea;
    @FXML private Button saveSalesBtn;
    
    // Expenses Tab
    @FXML private ComboBox<Employee> expensesEmployeeCombo;
    @FXML private TextField expensesAmountField;
    @FXML private DatePicker expensesDatePicker;
    @FXML private TextField expensesTimeField;
    @FXML private TextArea expensesNotesArea;
    @FXML private Button saveExpensesBtn;
    
    // Profits Tab
    @FXML private ComboBox<Employee> profitsEmployeeCombo;
    @FXML private TextField profitsAmountField;
    @FXML private DatePicker profitsDatePicker;
    @FXML private TextField profitsTimeField;
    @FXML private TextArea profitsNotesArea;
    @FXML private Button saveProfitsBtn;
    
    private DatabaseManager dbManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = DatabaseManager.getInstance();
        setupComponents();
        loadEmployees();
    }
    
    private void setupComponents() {
        // Set default values
        salesDatePicker.setValue(LocalDate.now());
        expensesDatePicker.setValue(LocalDate.now());
        profitsDatePicker.setValue(LocalDate.now());
        
        String currentTime = LocalTime.now().toString().substring(0, 5);
        salesTimeField.setText(currentTime);
        expensesTimeField.setText(currentTime);
        profitsTimeField.setText(currentTime);
        
        // Add input validation
        addNumericValidation(salesAmountField);
        addNumericValidation(expensesAmountField);
        addNumericValidation(profitsAmountField);
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                field.setText(oldValue);
            }
        });
    }
    
    private void loadEmployees() {
        var employees = FXCollections.observableArrayList(dbManager.getAllEmployees());
        salesEmployeeCombo.setItems(employees);
        expensesEmployeeCombo.setItems(employees);
        profitsEmployeeCombo.setItems(employees);
    }
    
    @FXML
    private void saveSales() {
        saveTransaction("sales", salesEmployeeCombo, salesAmountField, 
                       salesDatePicker, salesTimeField, salesNotesArea);
    }
    
    @FXML
    private void saveExpenses() {
        saveTransaction("expenses", expensesEmployeeCombo, expensesAmountField, 
                       expensesDatePicker, expensesTimeField, expensesNotesArea);
    }
    
    @FXML
    private void saveProfits() {
        saveTransaction("profits", profitsEmployeeCombo, profitsAmountField, 
                       profitsDatePicker, profitsTimeField, profitsNotesArea);
    }
    
    private void saveTransaction(String type, ComboBox<Employee> employeeCombo, 
                               TextField amountField, DatePicker datePicker, 
                               TextField timeField, TextArea notesArea) {
        
        if (!validateInput(employeeCombo, amountField, datePicker, timeField)) {
            return;
        }
        
        Transaction transaction = new Transaction();
        transaction.setEmployeeId(employeeCombo.getValue().getId());
        transaction.setAmount(Double.parseDouble(amountField.getText()));
        transaction.setDate(datePicker.getValue());
        transaction.setTime(timeField.getText());
        transaction.setNotes(notesArea.getText());
        
        boolean success = dbManager.saveTransaction(type, transaction);
        
        if (success) {
            AlertUtil.showSuccess("تم حفظ البيانات بنجاح");
            clearForm(employeeCombo, amountField, datePicker, timeField, notesArea);
        } else {
            AlertUtil.showError("حدث خطأ أثناء حفظ البيانات");
        }
    }
    
    private boolean validateInput(ComboBox<Employee> employeeCombo, TextField amountField, 
                                DatePicker datePicker, TextField timeField) {
        if (employeeCombo.getValue() == null) {
            AlertUtil.showWarning("يرجى اختيار الموظف");
            return false;
        }
        
        if (amountField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("يرجى إدخال المبلغ");
            return false;
        }
        
        if (datePicker.getValue() == null) {
            AlertUtil.showWarning("يرجى اختيار التاريخ");
            return false;
        }
        
        if (timeField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("يرجى إدخال الوقت");
            return false;
        }
        
        return true;
    }
    
    private void clearForm(ComboBox<Employee> employeeCombo, TextField amountField, 
                          DatePicker datePicker, TextField timeField, TextArea notesArea) {
        employeeCombo.setValue(null);
        amountField.clear();
        datePicker.setValue(LocalDate.now());
        timeField.setText(LocalTime.now().toString().substring(0, 5));
        notesArea.clear();
    }
    
    public void refreshEmployees() {
        loadEmployees();
    }
}