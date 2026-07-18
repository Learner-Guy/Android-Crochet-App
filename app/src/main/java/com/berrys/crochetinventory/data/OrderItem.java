package com.berrys.crochetinventory.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

@Entity(
    tableName = "order_items",
    foreignKeys = {
        @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "orderId", onDelete = CASCADE),
        @ForeignKey(entity = InventoryItem.class, parentColumns = "id", childColumns = "inventoryItemId", onDelete = SET_NULL)
    },
    indices = {
        @Index(value = "orderId"),
        @Index(value = "inventoryItemId")
    }
)
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int orderId;
    private int inventoryItemId;      // -1 if not from inventory
    private String customItemName;    // For items not in inventory
    private int quantityUsed;
    private double unitPrice;         // Price at time of order
    private double totalAmount;       // quantityUsed * unitPrice
    private String notes;

    public OrderItem(int orderId, int inventoryItemId, String customItemName,
                     int quantityUsed, double unitPrice, String notes) {
        this.orderId = orderId;
        this.inventoryItemId = inventoryItemId;
        this.customItemName = customItemName;
        this.quantityUsed = quantityUsed;
        this.unitPrice = unitPrice;
        this.totalAmount = quantityUsed * unitPrice;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(int inventoryItemId) { this.inventoryItemId = inventoryItemId; }
    public String getCustomItemName() { return customItemName; }
    public void setCustomItemName(String customItemName) { this.customItemName = customItemName; }
    public int getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(int quantityUsed) { this.quantityUsed = quantityUsed; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
