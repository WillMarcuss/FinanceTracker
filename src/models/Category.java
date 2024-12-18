package models;

public class Category {
    private String name;
    private String color; // For PieChart visualization

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
