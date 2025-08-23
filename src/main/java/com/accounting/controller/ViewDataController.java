package com.accounting.controller;

import com.accounting.database.DatabaseManager;
import com.accounting.model.TransactionView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewDataController implements Initializable {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Button filterBtn;
    @FXML private TabPane tabPane;

    // Sales table
    @FXML private TableView<TransactionView> salesTable;
    @FXML private TableColumn<TransactionView, String> salesEmployeeCol;
    @FXML private TableColumn<TransactionView, Double> salesAmountCol;
    @FXML private TableColumn<TransactionView, LocalDate> salesDateCol;
    @FXML private TableColumn<TransactionView, String> salesTimeCol;
    @FXML private TableColumn<TransactionView, String> salesNotesCol;
    @FXML private TableColumn<TransactionView, Void> salesActionCol;
    @FXML private Label salesTotalLabel;

    // Expenses table
    @FXML private TableView<TransactionView> expensesTable;
    @FXML private TableColumn<TransactionView, String> expensesEmployeeCol;
    @FXML private TableColumn<TransactionView, Double> expensesAmountCol;
    @FXML private TableColumn<TransactionView, LocalDate> expensesDateCol;
    @FXML private TableColumn<TransactionView, String> expensesTimeCol;
    @FXML private TableColumn<TransactionView, String> expensesNotesCol;
    @FXML private TableColumn<TransactionView, Void> expensesActionCol;
    @FXML private Label expensesTotalLabel;

    // Profits table
    @FXML private TableView<TransactionView> profitsTable;
    @FXML private TableColumn<TransactionView, String> profitsEmployeeCol;
    @FXML private TableColumn<TransactionView, Double> profitsAmountCol;
    @FXML private TableColumn<TransactionView, LocalDate> profitsDateCol;
    @FXML private TableColumn<TransactionView, String> profitsTimeCol;
    @FXML private TableColumn<TransactionView, String> profitsNotesCol;
    @FXML private TableColumn<TransactionView, Void> profitsActionCol;
    @FXML private Label profitsTotalLabel;

    // Summary labels
    @FXML private Label summaryTotalSales;
    @FXML private Label summaryTotalExpenses;
    @FXML private Label summaryTotalProfits;
    @FXML private Label summaryNetProfit;

    private DatabaseManager dbManager;
    private NumberFormat currencyFormat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = DatabaseManager.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        // Set default date range (current month)
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);

        setupTables();
        loadData();
    }

    private void setupTables() {
        // Setup Sales Table
        setupTable(salesTable, salesEmployeeCol, salesAmountCol,
                salesDateCol, salesTimeCol, salesNotesCol, salesActionCol, "sales");

        // Setup Expenses Table
        setupTable(expensesTable, expensesEmployeeCol, expensesAmountCol,
                expensesDateCol, expensesTimeCol, expensesNotesCol, expensesActionCol, "expenses");

        // Setup Profits Table
        setupTable(profitsTable, profitsEmployeeCol, profitsAmountCol,
                profitsDateCol, profitsTimeCol, profitsNotesCol, profitsActionCol, "profits");
    }

    private void setupTable(TableView<TransactionView> table,
                            TableColumn<TransactionView, String> employeeCol,
                            TableColumn<TransactionView, Double> amountCol,
                            TableColumn<TransactionView, LocalDate> dateCol,
                            TableColumn<TransactionView, String> timeCol,
                            TableColumn<TransactionView, String> notesCol,
                            TableColumn<TransactionView, Void> actionCol,
                            String tableType) {

        // Set cell value factories
        employeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Format amount column
        amountCol.setCellFactory(column -> new TableCell<TransactionView, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount));
                }
            }
        });

        // Format date column
        dateCol.setCellFactory(column -> new TableCell<TransactionView, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });

        // Setup action column with delete button
        setupActionColumn(actionCol, table, tableType);

        // Set column alignment for RTL
        employeeCol.getStyleClass().add("align-right");
        notesCol.getStyleClass().add("align-right");
    }

    private void setupActionColumn(TableColumn<TransactionView, Void> actionCol,
                                   TableView<TransactionView> table, String tableType) {
        Callback<TableColumn<TransactionView, Void>, TableCell<TransactionView, Void>> cellFactory =
                new Callback<TableColumn<TransactionView, Void>, TableCell<TransactionView, Void>>() {
                    @Override
                    public TableCell<TransactionView, Void> call(final TableColumn<TransactionView, Void> param) {
                        final TableCell<TransactionView, Void> cell = new TableCell<TransactionView, Void>() {

                            private final Button deleteBtn = new Button("حذف");

                            {
                                deleteBtn.getStyleClass().addAll("delete-button", "danger-button");
                                deleteBtn.setOnAction(event -> {
                                    TransactionView transaction = getTableView().getItems().get(getIndex());
                                    handleDeleteTransaction(transaction, tableType);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(deleteBtn);
                                }
                            }
                        };
                        return cell;
                    }
                };

        actionCol.setCellFactory(cellFactory);
    }

    private void handleDeleteTransaction(TransactionView transaction, String tableType) {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("تأكيد الحذف");
        confirmAlert.setHeaderText("حذف المعاملة");
        confirmAlert.setContentText("هل أنت متأكد من رغبتك في حذف هذه المعاملة؟\n" +
                "الموظف: " + transaction.getEmployeeName() + "\n" +
                "المبلغ: " + currencyFormat.format(transaction.getAmount()) + "\n" +
                "التاريخ: " + transaction.getDate());

        // Add Arabic button text
        ButtonType deleteButtonType = new ButtonType("حذف", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("إلغاء", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmAlert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == deleteButtonType) {
            boolean success = dbManager.deleteTransaction(tableType, transaction.getId());

            if (success) {
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("نجح الحذف");
                successAlert.setHeaderText(null);
                successAlert.setContentText("تم حذف المعاملة بنجاح.");
                successAlert.showAndWait();

                // Refresh the data
                loadData();
            } else {
                // Show error message
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("خطأ في الحذف");
                errorAlert.setHeaderText("فشل في حذف المعاملة");
                errorAlert.setContentText("حدث خطأ أثناء محاولة حذف المعاملة. يرجى المحاولة مرة أخرى.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void applyFilter() {
        loadData();
    }

    private void loadData() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("خطأ في التاريخ", "يرجى تحديد تاريخ البداية والنهاية.");
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("خطأ في التاريخ", "تاريخ البداية يجب أن يكون قبل تاريخ النهاية.");
            return;
        }

        // Load sales data
        List<TransactionView> salesData = dbManager.getTransactionViews("sales", fromDate, toDate);
        salesTable.getItems().clear();
        salesTable.getItems().addAll(salesData);

        // Load expenses data
        List<TransactionView> expensesData = dbManager.getTransactionViews("expenses", fromDate, toDate);
        expensesTable.getItems().clear();
        expensesTable.getItems().addAll(expensesData);

        // Load profits data
        List<TransactionView> profitsData = dbManager.getTransactionViews("profits", fromDate, toDate);
        profitsTable.getItems().clear();
        profitsTable.getItems().addAll(profitsData);

        // Update totals
        updateTotals(salesData, expensesData, profitsData);
    }

    private void updateTotals(List<TransactionView> salesData,
                              List<TransactionView> expensesData,
                              List<TransactionView> profitsData) {

        // Calculate totals
        double totalSales = salesData.stream().mapToDouble(TransactionView::getAmount).sum();
        double totalExpenses = expensesData.stream().mapToDouble(TransactionView::getAmount).sum();
        double totalProfits = profitsData.stream().mapToDouble(TransactionView::getAmount).sum();
        double netProfit = totalSales + totalProfits - totalExpenses;

        // Update individual table totals
        salesTotalLabel.setText("الإجمالي: " + currencyFormat.format(totalSales));
        expensesTotalLabel.setText("الإجمالي: " + currencyFormat.format(totalExpenses));
        profitsTotalLabel.setText("الإجمالي: " + currencyFormat.format(totalProfits));

        // Update summary
        summaryTotalSales.setText(currencyFormat.format(totalSales));
        summaryTotalExpenses.setText(currencyFormat.format(totalExpenses));
        summaryTotalProfits.setText(currencyFormat.format(totalProfits));
        summaryNetProfit.setText(currencyFormat.format(netProfit));

        // Apply styles based on values
        updateSummaryStyles(netProfit);
    }

    private void updateSummaryStyles(double netProfit) {
        // Remove existing style classes
        summaryNetProfit.getStyleClass().removeAll("positive", "negative", "neutral");

        // Apply appropriate style
        if (netProfit > 0) {
            summaryNetProfit.getStyleClass().add("positive");
        } else if (netProfit < 0) {
            summaryNetProfit.getStyleClass().add("negative");
        } else {
            summaryNetProfit.getStyleClass().add("neutral");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}