package com.berrys.crochetinventory.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "business_profile")
public class BusinessProfile {
    @PrimaryKey
    private int id = 1;  // Single row only
    private String businessName;
    private String address;
    private String phone;
    private String email;
    private String gstNumber;
    private String logoPath;
    private String termsAndConditions;
    private String billPrefix;      // e.g., "BILL" → BILL-001
    private int lastBillNumber;
    private String upiId;           // For payment QR code

    public BusinessProfile(String businessName, String address, String phone,
                           String email, String gstNumber, String logoPath,
                           String termsAndConditions, String billPrefix) {
        this.businessName = businessName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.gstNumber = gstNumber;
        this.logoPath = logoPath;
        this.termsAndConditions = termsAndConditions;
        this.billPrefix = billPrefix;
        this.lastBillNumber = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    public String getBillPrefix() { return billPrefix; }
    public void setBillPrefix(String billPrefix) { this.billPrefix = billPrefix; }
    public int getLastBillNumber() { return lastBillNumber; }
    public void setLastBillNumber(int lastBillNumber) { this.lastBillNumber = lastBillNumber; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getNextBillNumber() {
        lastBillNumber++;
        return billPrefix + "-" + String.format("%04d", lastBillNumber);
    }
}
