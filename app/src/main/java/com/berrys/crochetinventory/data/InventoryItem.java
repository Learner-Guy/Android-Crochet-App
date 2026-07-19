package com.berrys.crochetinventory.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Date;

@Entity(tableName = "inventory")
@TypeConverters(DateConverter.class)
public class InventoryItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String category;
    private String color;
    private int quantity;
    private String unit;
    private double cost;
    private String supplier;
    private int lowStock;
    private String notes;
    private String imagePath;
    private String iconName;
    private Date createdDate;

    // Size fields
    private String itemType;      // "DISCRETE" or "CONTINUOUS"
    private double sizeValue;     // e.g., 5.0 (meters), 100.0 (grams)
    private String sizeUnit;      // "mm", "cm", "m", "g", "kg", "yards", "inches"

    public InventoryItem(String name, String category, String color, int quantity,
                         String unit, double cost, String supplier, int lowStock,
                         String notes, String imagePath, String iconName, Date createdDate) {
        this.name = name;
        this.category = category;
        this.color = color;
        this.quantity = quantity;
        this.unit = unit;
        this.cost = cost;
        this.supplier = supplier;
        this.lowStock = lowStock;
        this.notes = notes;
        this.imagePath = imagePath;
        this.iconName = iconName;
        this.createdDate = createdDate;
        this.itemType = "DISCRETE";
        this.sizeValue = 0;
        this.sizeUnit = "";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public int getLowStock() { return lowStock; }
    public void setLowStock(int lowStock) { this.lowStock = lowStock; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    // Size getters/setters
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public double getSizeValue() { return sizeValue; }
    public void setSizeValue(double sizeValue) { this.sizeValue = sizeValue; }
    public String getSizeUnit() { return sizeUnit; }
    public void setSizeUnit(String sizeUnit) { this.sizeUnit = sizeUnit; }

    // Helper: get display string for size
    public String getSizeDisplay() {
        if ("CONTINUOUS".equals(itemType) && sizeValue > 0) {
            return sizeValue + " " + sizeUnit;
        }
        return "";
    }

    // Helper: check if this is a continuous item
    public boolean isContinuous() {
        return "CONTINUOUS".equals(itemType);
    }
}
