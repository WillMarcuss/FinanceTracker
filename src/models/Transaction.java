package models;

import java.time.LocalDate;

public class Transaction {
    private int id; // Add this field
    private String description;
    private double amount;
    private String category;
    private LocalDate date;

    public Transaction(int id, String description, double amount, String category, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Transaction(String description, double amount, String category, LocalDate date) {
        this(-1, description, amount, category, date); // Default id for new transactions
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
