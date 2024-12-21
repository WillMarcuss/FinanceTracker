package utils;

import controllers.LoginController;
import models.Budget;
import models.Transaction;
import at.favre.lib.crypto.bcrypt.BCrypt;
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
            // Create transactions table
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "description TEXT, " +
                    "amount REAL, " +
                    "category TEXT, " +
                    "date TEXT, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            conn.createStatement().execute(transactionsTable);

            // Create budgets table
            String budgetsTable = "CREATE TABLE IF NOT EXISTS budgets (" +
                    "user_id INTEGER, " +
                    "category TEXT PRIMARY KEY, " +
                    "amount REAL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";
            conn.createStatement().execute(budgetsTable);

            // Create users table
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)";
            conn.createStatement().execute(usersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Add or update a budget
    public static void setBudget(String category, double amount) {
        int userId = LoginController.getLoggedInUserId(); // Get logged-in user ID
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO budgets (user_id, category, amount) VALUES (?, ?, ?) " +
                    "ON CONFLICT(user_id, category) DO UPDATE SET amount = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Budget> getBudgets() {
        List<Budget> budgets = new ArrayList<>();
        int userId = LoginController.getLoggedInUserId(); // Get logged-in user ID
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM budgets WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
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
        int userId = LoginController.getLoggedInUserId();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM transactions WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static void addTransaction(Transaction transaction) {
        int userId = LoginController.getLoggedInUserId(); // Get logged-in user ID
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO transactions (user_id, description, amount, category, date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, transaction.getDescription());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCategory());
            pstmt.setString(5, transaction.getDate().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updateTransaction(Transaction transaction) {
        int userId = LoginController.getLoggedInUserId();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE transactions SET description = ?, amount = ?, category = ?, date = ? " +
                    "WHERE id = ? AND user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, transaction.getDescription());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setString(4, transaction.getDate().toString());
            pstmt.setInt(5, transaction.getId());
            pstmt.setInt(6, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteTransaction(int id) {
        int userId = LoginController.getLoggedInUserId(); // Get logged-in user ID
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBudget(String category) {
        int userId = LoginController.getLoggedInUserId(); // Get logged-in user ID
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM budgets WHERE category = ? AND user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT id, password FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedPassword);
                if (result.verified) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if authentication fails
    }


    public static boolean doesUsernameExist(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Returns false if query fails or username does not exist
    }


}
