package utils;

import models.Budget;
import models.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:finance.db";

    static {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    // Create a budgets table
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Existing transactions table
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "description TEXT, " +
                    "amount REAL, " +
                    "category TEXT, " +
                    "date TEXT)";
            conn.createStatement().execute(transactionsTable);

            // New budgets table
            String budgetsTable = "CREATE TABLE IF NOT EXISTS budgets (" +
                    "category TEXT PRIMARY KEY, " +
                    "amount REAL)";
            conn.createStatement().execute(budgetsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add or update a budget
    public static void setBudget(String category, double amount) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO budgets (category, amount) VALUES (?, ?) " +
                    "ON CONFLICT(category) DO UPDATE SET amount = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all budgets
    public static List<Budget> getBudgets() {
        List<Budget> budgets = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM budgets";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                budgets.add(new Budget(rs.getString("category"), rs.getDouble("amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }


    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM transactions";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("id"), // Fetch id
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date"))
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static void addTransaction(Transaction transaction) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO transactions (description, amount, category, date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, transaction.getDescription());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setString(4, transaction.getDate().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTransaction(Transaction transaction) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE transactions SET description = ?, amount = ?, category = ?, date = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, transaction.getDescription());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setString(4, transaction.getDate().toString());
            pstmt.setInt(5, transaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTransaction(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM transactions WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteBudget(String category) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM budgets WHERE category = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
