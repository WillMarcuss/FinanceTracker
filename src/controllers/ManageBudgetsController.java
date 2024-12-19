package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Budget;
import utils.DatabaseHelper;

public class ManageBudgetsController {
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> categoryColumn;
    @FXML private TableColumn<Budget, Double> amountColumn;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;

    @FXML
    public void initialize() {
        // Bind columns to Budget properties
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Load budgets into the table
        loadBudgets();
    }

    @FXML
    private void onSaveBudgetClicked() {
        String category = categoryField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid budget amount!");
            return;
        }

        DatabaseHelper.setBudget(category, amount);
        loadBudgets();
    }

    private void loadBudgets() {
        budgetTable.getItems().setAll(DatabaseHelper.getBudgets());
    }
}
