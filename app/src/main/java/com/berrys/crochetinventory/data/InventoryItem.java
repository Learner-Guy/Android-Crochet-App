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
    private Date createdDate;

    public InventoryItem(String name, String category, String color, int quantity,
                         String unit, double cost, String supplier, int lowStock,
                         String notes, String imagePath, Date createdDate) {
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
        this.createdDate = createdDate;
    }

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
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}