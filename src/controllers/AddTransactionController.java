package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Transaction;
import utils.DatabaseHelper;

import java.time.LocalDate;

public class AddTransactionController {
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    private Transaction editingTransaction;

    public void setTransaction(Transaction transaction) {
        this.editingTransaction = transaction;
        descriptionField.setText(transaction.getDescription());
        amountField.setText(String.valueOf(transaction.getAmount()));
        categoryComboBox.setValue(transaction.getCategory());
        datePicker.setValue(transaction.getDate());
    }

    @FXML
    private void onSaveClicked() {
        try {
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryComboBox.getValue();
            LocalDate date = datePicker.getValue();

            if (editingTransaction != null) {
                // Update the existing transaction
                editingTransaction.setDescription(description);
                editingTransaction.setAmount(amount);
                editingTransaction.setCategory(category);
                editingTransaction.setDate(date);

                DatabaseHelper.updateTransaction(editingTransaction);
            } else {
                // Create a new transaction
                Transaction transaction = new Transaction(description, amount, category, date);
                DatabaseHelper.addTransaction(transaction);
            }

            // Close the window
            ((Stage) descriptionField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("Food", "Transportation", "Entertainment", "Utilities", "Other");
    }




}
