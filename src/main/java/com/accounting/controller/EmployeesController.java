package com.accounting.controller;

import com.accounting.database.DatabaseManager;
import com.accounting.model.Employee;
import com.accounting.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeesController implements Initializable {
    
    @FXML private TextField nameField;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Button clearBtn;
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> idCol;
    @FXML private TableColumn<Employee, String> nameCol;
    
    private DatabaseManager dbManager;
    private Employee selectedEmployee;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = DatabaseManager.getInstance();
        setupTable();
        loadEmployees();
        setupButtons();
    }
    
    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        employeesTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedEmployee = newSelection;
                if (newSelection != null) {
                    nameField.setText(newSelection.getName());
                    updateBtn.setDisable(false);
                    deleteBtn.setDisable(false);
                } else {
                    clearForm();
                }
            });
    }
    
    private void setupButtons() {
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }
    
    @FXML
    private void addEmployee() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.showWarning("يرجى إدخال اسم الموظف");
            return;
        }
        
        Employee employee = new Employee();
        employee.setName(name);
        
        boolean success = dbManager.saveEmployee(employee);
        if (success) {
            AlertUtil.showSuccess("تم إضافة الموظف بنجاح");
            loadEmployees();
            clearForm();
        } else {
            AlertUtil.showError("حدث خطأ أثناء إضافة الموظف");
        }
    }
    
    @FXML
    private void updateEmployee() {
        if (selectedEmployee == null) return;
        
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.showWarning("يرجى إدخال اسم الموظف");
            return;
        }
        
        selectedEmployee.setName(name);
        boolean success = dbManager.updateEmployee(selectedEmployee);
        
        if (success) {
            AlertUtil.showSuccess("تم تحديث بيانات الموظف بنجاح");
            loadEmployees();
            clearForm();
        } else {
            AlertUtil.showError("حدث خطأ أثناء تحديث بيانات الموظف");
        }
    }
    
    @FXML
    private void deleteEmployee() {
        if (selectedEmployee == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الحذف");
        alert.setHeaderText("هل أنت متأكد من حذف هذا الموظف؟");
        alert.setContentText("سيتم حذف جميع البيانات المرتبطة بهذا الموظف");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = dbManager.deleteEmployee(selectedEmployee.getId());
            
            if (success) {
                AlertUtil.showSuccess("تم حذف الموظف بنجاح");
                loadEmployees();
                clearForm();
            } else {
                AlertUtil.showError("حدث خطأ أثناء حذف الموظف");
            }
        }
    }
    
    @FXML
    private void clearForm() {
        nameField.clear();
        employeesTable.getSelectionModel().clearSelection();
        selectedEmployee = null;
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
    }
    
    private void loadEmployees() {
        var employees = FXCollections.observableArrayList(dbManager.getAllEmployees());
        employeesTable.setItems(employees);
    }
}