package utils;

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

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "description TEXT, " +
                    "amount REAL, " +
                    "category TEXT, " +
                    "date TEXT)";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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


}
