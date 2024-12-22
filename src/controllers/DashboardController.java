package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Budget;
import models.Transaction;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import utils.DatabaseHelper;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private PieChart summaryChart;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis lineChartXAxis;
    @FXML private NumberAxis lineChartYAxis;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis barChartXAxis;
    @FXML private NumberAxis barChartYAxis;
    @FXML private Pane heatMapPane;
    private TabPane tabPane;


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
        applyFadeTransition(tabPane, 0.5); // Smooth transition for TabPane
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
        loadLineChart();
        loadBarChart();
        loadHeatMap();
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
            loadBarChart();
            loadLineChart();
            loadHeatMap();
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
            loadLineChart();
            loadHeatMap();
            loadBarChart();
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
            loadLineChart();
            loadHeatMap();
            loadBarChart();
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
    private void loadLineChart() {
        lineChart.getData().clear(); // Clear existing data

        List<Transaction> transactions = DatabaseHelper.getTransactions();
        Map<String, Double> dateSpendingMap = new TreeMap<>(); // Sorted by date

        for (Transaction transaction : transactions) {
            String date = transaction.getDate().toString();
            dateSpendingMap.put(date, dateSpendingMap.getOrDefault(date, 0.0) + transaction.getAmount());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Spending");

        for (Map.Entry<String, Double> entry : dateSpendingMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);
    }

    private void loadBarChart() {
        barChart.getData().clear(); // Clear existing data

        List<Transaction> transactions = DatabaseHelper.getTransactions();
        Map<String, Double> categorySpendingMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            categorySpendingMap.put(category, categorySpendingMap.getOrDefault(category, 0.0) + transaction.getAmount());
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Category Spending");

        for (Map.Entry<String, Double> entry : categorySpendingMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    private void loadHeatMap() {
        heatMapPane.getChildren().clear();

        List<Transaction> transactions = DatabaseHelper.getTransactions();
        Map<String, Double> dailySpending = new HashMap<>();

        // Sum up spending for each day
        for (Transaction transaction : transactions) {
            String date = transaction.getDate().toString();
            dailySpending.put(date, dailySpending.getOrDefault(date, 0.0) + transaction.getAmount());
        }

        // Create a grid for the heat map
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Get the current month and year
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);

        // Calculate the starting column for the first day of the month
        int startingColumn = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0, Saturday = 6

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            int dayOfWeek = date.getDayOfWeek().getValue() % 7; // Sunday = 0, Saturday = 6
            int weekOfMonth = (day + startingColumn - 1) / 7;   // Calculate the row (week) of the month

            // Get spending for the current day
            String dateStr = date.toString();
            double amount = dailySpending.getOrDefault(dateStr, 0.0);

            // Create a visual box for the date
            Label label = new Label(String.valueOf(day));
            Rectangle box = new Rectangle(50, 50);
            int colorIntensity = (int) Math.min(255, amount * 10); // Adjust color intensity based on spending
            box.setFill(Color.rgb(255, 255 - colorIntensity, 255 - colorIntensity));

            // Add tooltip for spending details
            Tooltip tooltip = new Tooltip("Date: " + dateStr + "\nSpending: $" + amount);
            Tooltip.install(box, tooltip);

            // Add the visual to the grid
            VBox vbox = new VBox(box, label);
            gridPane.add(vbox, dayOfWeek, weekOfMonth); // Correct column and row placement
        }

        heatMapPane.getChildren().add(gridPane);
    }
    @FXML
    private void onLogoutClicked() {
        try {
            Stage stage = (Stage) transactionTable.getScene().getWindow(); // Get the current stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            stage.setScene(loginScene);
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyFadeTransition(Node node, double durationInSeconds) {
        FadeTransition fade = new FadeTransition(Duration.seconds(durationInSeconds), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void onImportTransactionsClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Transactions");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length == 4) {
                        Transaction transaction = new Transaction(
                                0,
                                data[0], // Description
                                Double.parseDouble(data[1]), // Amount
                                data[2], // Category
                                LocalDate.parse(data[3]) // Date
                        );
                        DatabaseHelper.addTransaction(transaction);
                    }
                }
                loadTransactions();
                loadSummaryChart();
                loadBarChart();
                loadLineChart();
                loadHeatMap();
                System.out.println("Transactions imported successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onExportToPDFClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Finance Tracker - Transaction Report");
                contentStream.newLineAtOffset(0, -20);

                double totalExpenses = DatabaseHelper.getTransactions()
                        .stream().mapToDouble(Transaction::getAmount).sum();

                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                contentStream.showText("Total Expenses: $" + totalExpenses);
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Transactions:");
                contentStream.newLineAtOffset(0, -20);

                for (Transaction t : DatabaseHelper.getTransactions()) {
                    String line = String.format("%s | $%.2f | %s | %s",
                            t.getDescription(), t.getAmount(), t.getCategory(), t.getDate());
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.endText();
                contentStream.close();

                document.save(file);
                document.close();
                System.out.println("PDF report generated successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
