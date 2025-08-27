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
        long startTime = System.currentTimeMillis();
        System.out.println("  - DatabaseManager: Creating database connection...");

        try {
            // Add SQLite performance optimizations
            connection = DriverManager.getConnection(DB_URL + "?journal_mode=WAL&synchronous=NORMAL&cache_size=10000&temp_store=memory");

            // Set additional performance pragmas
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
                stmt.execute("PRAGMA synchronous=NORMAL");
                stmt.execute("PRAGMA cache_size=10000");
                stmt.execute("PRAGMA temp_store=memory");
                stmt.execute("PRAGMA mmap_size=268435456"); // 256MB
            }

            System.out.println("  - DatabaseManager: Connection established in " +
                    (System.currentTimeMillis() - startTime) + "ms");

        } catch (SQLException e) {
            System.err.println("  - DatabaseManager: Connection failed in " +
                    (System.currentTimeMillis() - startTime) + "ms");
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        long startTime = System.currentTimeMillis();
        System.out.println("  - DatabaseManager: Getting instance...");

        if (instance == null) {
            System.out.println("  - DatabaseManager: Creating new instance...");
            instance = new DatabaseManager();
        } else {
            System.out.println("  - DatabaseManager: Using existing instance");
        }

        System.out.println("  - DatabaseManager: getInstance completed in " +
                (System.currentTimeMillis() - startTime) + "ms");
        return instance;
    }

    public void initializeDatabase() {
        long startTime = System.currentTimeMillis();
        System.out.println("  - DatabaseManager: Starting database initialization...");

        createTables();

        System.out.println("  - DatabaseManager: Database initialization completed in " +
                (System.currentTimeMillis() - startTime) + "ms");
    }

    private void createTables() {
        long startTime = System.currentTimeMillis();
        System.out.println("  - DatabaseManager: Creating tables...");

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
            // Use a single transaction for all table creation
            connection.setAutoCommit(false);

            long tableCreationStart = System.currentTimeMillis();

            for (int i = 0; i < createTableQueries.length; i++) {
                long tableStart = System.currentTimeMillis();
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(createTableQueries[i]);
                }
                System.out.println("  - DatabaseManager: Table " + (i+1) + " created in " +
                        (System.currentTimeMillis() - tableStart) + "ms");
            }

            System.out.println("  - DatabaseManager: All tables created in " +
                    (System.currentTimeMillis() - tableCreationStart) + "ms");

            // Insert default settings
            long settingsStart = System.currentTimeMillis();
            insertDefaultSettings();
            System.out.println("  - DatabaseManager: Default settings inserted in " +
                    (System.currentTimeMillis() - settingsStart) + "ms");

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            System.err.println("  - DatabaseManager: Error creating tables in " +
                    (System.currentTimeMillis() - startTime) + "ms");
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }

        System.out.println("  - DatabaseManager: createTables completed in " +
                (System.currentTimeMillis() - startTime) + "ms");
    }

    private void insertDefaultSettings() {
        String insertSettings = """
            INSERT OR IGNORE INTO settings (key, value) VALUES 
            ('currency', 'Kz'),
            ('exchange_rate', '1.0')
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

    public boolean deleteTransaction(String type, int transactionId) {
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
        long startTime = System.currentTimeMillis();
        System.out.println("  - DatabaseManager: Closing connection...");

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("  - DatabaseManager: Connection closed in " +
                (System.currentTimeMillis() - startTime) + "ms");
    }
}