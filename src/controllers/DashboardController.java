package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Budget;
import models.Transaction;
import utils.DatabaseHelper;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private PieChart summaryChart;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;

    @FXML
    private void onSearchTransactions() {
        String query = searchField.getText().toLowerCase();
        List<Transaction> transactions = DatabaseHelper.getTransactions();
        List<Transaction> filtered = transactions.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(query))
                .toList();
        transactionTable.getItems().setAll(filtered);
    }

    @FXML
    private void onFilterTransactions() {
        String selectedCategory = categoryFilter.getValue();

        // If "All Categories" is selected, display all transactions
        if (selectedCategory.equals("All Categories")) {
            loadTransactions();
            return;
        }

        // Filter transactions by the selected category
        List<Transaction> transactions = DatabaseHelper.getTransactions();
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(transaction -> transaction.getCategory().equals(selectedCategory))
                .toList();

        transactionTable.getItems().setAll(filteredTransactions);
    }


    @FXML
    private void initialize() {
        // Bind the TableView columns to Transaction properties
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        // Populate the ComboBox with categories
        populateCategoryFilter();

        // Load data into the TableView and PieChart
        loadTransactions();
        loadSummaryChart();
    }




    private void loadTransactions() {
        List<Transaction> transactions = DatabaseHelper.getTransactions();
        transactionTable.getItems().setAll(transactions);
    }

    private void loadSummaryChart() {
        summaryChart.getData().clear();

        // Fetch budgets and calculate usage
        List<Budget> budgets = DatabaseHelper.getBudgets();
        List<Transaction> transactions = DatabaseHelper.getTransactions();

        for (Budget budget : budgets) {
            double totalSpent = transactions.stream()
                    .filter(t -> t.getCategory().equals(budget.getCategory()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double remainingBudget = budget.getAmount() - totalSpent;
            String label = budget.getCategory() + " (Remaining: " + remainingBudget + ")";
            summaryChart.getData().add(new PieChart.Data(label, remainingBudget));
        }
    }



    @FXML
    private void onAddTransactionClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTransaction.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Transaction");
            stage.setScene(scene);
            stage.showAndWait();

            // Refresh the transaction table after the new transaction is added
            loadTransactions();
            loadSummaryChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditTransactionClicked() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            System.out.println("No transaction selected!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTransaction.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass the selected transaction to the controller
            AddTransactionController controller = loader.getController();
            controller.setTransaction(selectedTransaction);

            Stage stage = new Stage();
            stage.setTitle("Edit Transaction");
            stage.setScene(scene);
            stage.showAndWait();

            // Refresh the transaction table after editing
            loadTransactions();
            loadSummaryChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteTransactionClicked() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            System.out.println("No transaction selected!");
            return;
        }

        try {
            DatabaseHelper.deleteTransaction(selectedTransaction.getId());
            loadTransactions();
            loadSummaryChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExportToCSVClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Description,Amount,Category,Date");
                for (Transaction t : DatabaseHelper.getTransactions()) {
                    writer.println(String.format("%s,%s,%s,%s",
                            t.getDescription(),
                            t.getAmount(),
                            t.getCategory(),
                            t.getDate()));
                }
                System.out.println("Export successful!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateCategoryFilter() {
        categoryFilter.getItems().clear();

        // Add predefined categories
        categoryFilter.getItems().addAll("Food", "Transportation", "Entertainment", "Utilities", "Other");

        // Add an "All Categories" option
        categoryFilter.getItems().add("All Categories");

        // Set default selection to "All Categories"
        categoryFilter.setValue("All Categories");
    }

    @FXML
    private void onManageBudgetsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ManageBudgets.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Manage Budgets");
            stage.setScene(scene);
            stage.showAndWait();

            // Refresh the PieChart or data if necessary
            loadSummaryChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
