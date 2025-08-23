package com.accounting.database;

import com.accounting.model.Employee;
import com.accounting.model.Transaction;
import com.accounting.model.TransactionView;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:accounting.db";

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void initializeDatabase() {
        createTables();
    }

    private void createTables() {
        String[] createTableQueries = {
                """
            CREATE TABLE IF NOT EXISTS employees (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS sales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY (employee_id) REFERENCES employees (id)
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY (employee_id) REFERENCES employees (id)
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS profits (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY (employee_id) REFERENCES employees (id)
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS settings (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """
        };

        try {
            for (String query : createTableQueries) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(query);
                }
            }

            // Insert default settings
            insertDefaultSettings();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultSettings() {
        String insertSettings = """
            INSERT OR IGNORE INTO settings (key, value) VALUES 
            ('currency', 'USD'),
            ('exchange_rate', '89500.0')
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertSettings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Employee operations
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id, name FROM employees ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public boolean saveEmployee(Employee employee) {
        String query = "INSERT INTO employees (name) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE employees SET name = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int employeeId) {
        try {
            connection.setAutoCommit(false);

            // Delete from all transaction tables
            String[] deleteQueries = {
                    "DELETE FROM sales WHERE employee_id = ?",
                    "DELETE FROM expenses WHERE employee_id = ?",
                    "DELETE FROM profits WHERE employee_id = ?",
                    "DELETE FROM employees WHERE id = ?"
            };

            for (String query : deleteQueries) {
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, employeeId);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Transaction operations
    public boolean saveTransaction(String type, Transaction transaction) {
        String query = String.format(
                "INSERT INTO %s (employee_id, amount, date, time, notes) VALUES (?, ?, ?, ?, ?)",
                type);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, transaction.getEmployeeId());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getDate().toString());
            pstmt.setString(4, transaction.getTime());
            pstmt.setString(5, transaction.getNotes());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // NEW METHOD: Delete a single transaction record
    public boolean deleteTransaction(String type, int transactionId) {
        // Validate table type to prevent SQL injection
        if (!isValidTableType(type)) {
            System.err.println("Invalid table type: " + type);
            return false;
        }

        String query = String.format("DELETE FROM %s WHERE id = ?", type);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to validate table types
    private boolean isValidTableType(String type) {
        return type.equals("sales") || type.equals("expenses") || type.equals("profits");
    }

    public List<TransactionView> getTransactionViews(String type, LocalDate fromDate, LocalDate toDate) {
        List<TransactionView> transactions = new ArrayList<>();
        String query = String.format("""
            SELECT t.id, e.name as employee_name, t.amount, t.date, t.time, t.notes
            FROM %s t
            JOIN employees e ON t.employee_id = e.id
            WHERE t.date BETWEEN ? AND ?
            ORDER BY t.date DESC, t.time DESC
        """, type);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, fromDate.toString());
            pstmt.setString(2, toDate.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TransactionView transaction = new TransactionView();
                    transaction.setId(rs.getInt("id"));
                    transaction.setEmployeeName(rs.getString("employee_name"));
                    transaction.setAmount(rs.getDouble("amount"));
                    transaction.setDate(LocalDate.parse(rs.getString("date")));
                    transaction.setTime(rs.getString("time"));
                    transaction.setNotes(rs.getString("notes"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // Settings operations
    public String getSetting(String key) {
        String query = "SELECT value FROM settings WHERE key = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateSetting(String key, String value) {
        String query = "INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}