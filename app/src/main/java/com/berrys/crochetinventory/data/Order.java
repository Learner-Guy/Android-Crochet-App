package com.berrys.crochetinventory.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Date;

@Entity(tableName = "orders")
@TypeConverters(DateConverter.class)
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String description;
    private double estimatedPrice;
    private double finalPrice;
    private double discountPercent;
    private double discountAmount;
    private double effectivePrice;
    private boolean isGstApplicable;
    private double gstPercent;
    private double taxAmount;
    private double grandTotal;
    private String status; // PENDING_CONFIRMATION, CONFIRMED, IN_PROGRESS, COMPLETED, READY_TO_DELIVER, DELIVERED, CANCELLED, REFUNDED
    private String sampleImagePath;
    private Date orderDate;
    private Date deliveryDate;
    private Date completedDate;
    private String cancellationReason;
    private Date cancelledDate;
    private String notes;
    private boolean isEstimateGenerated;
    private boolean isFinalBillGenerated;
    private String estimateBillPath;
    private String finalBillPath;

    public Order(String customerName, String customerPhone, String customerEmail,
                 String description, double estimatedPrice, Date orderDate,
                 Date deliveryDate, String notes) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.description = description;
        this.estimatedPrice = estimatedPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.notes = notes;
        this.status = OrderStatus.PENDING_CONFIRMATION.name();
        this.discountPercent = 0;
        this.discountAmount = 0;
        this.effectivePrice = estimatedPrice;
        this.isGstApplicable = false;
        this.gstPercent = 0;
        this.taxAmount = 0;
        this.grandTotal = estimatedPrice;
        this.isEstimateGenerated = false;
        this.isFinalBillGenerated = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(double estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getEffectivePrice() { return effectivePrice; }
    public void setEffectivePrice(double effectivePrice) { this.effectivePrice = effectivePrice; }
    public boolean isGstApplicable() { return isGstApplicable; }
    public void setGstApplicable(boolean gstApplicable) { isGstApplicable = gstApplicable; }
    public double getGstPercent() { return gstPercent; }
    public void setGstPercent(double gstPercent) { this.gstPercent = gstPercent; }
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSampleImagePath() { return sampleImagePath; }
    public void setSampleImagePath(String sampleImagePath) { this.sampleImagePath = sampleImagePath; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }
    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public Date getCancelledDate() { return cancelledDate; }
    public void setCancelledDate(Date cancelledDate) { this.cancelledDate = cancelledDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isEstimateGenerated() { return isEstimateGenerated; }
    public void setEstimateGenerated(boolean estimateGenerated) { isEstimateGenerated = estimateGenerated; }
    public boolean isFinalBillGenerated() { return isFinalBillGenerated; }
    public void setFinalBillGenerated(boolean finalBillGenerated) { isFinalBillGenerated = finalBillGenerated; }
    public String getEstimateBillPath() { return estimateBillPath; }
    public void setEstimateBillPath(String estimateBillPath) { this.estimateBillPath = estimateBillPath; }
    public String getFinalBillPath() { return finalBillPath; }
    public void setFinalBillPath(String finalBillPath) { this.finalBillPath = finalBillPath; }
}
