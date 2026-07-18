package com.berrys.crochetinventory.data;

public enum OrderStatus {
    PENDING_CONFIRMATION("Waiting for client confirmation", "#FF9800"),
    CONFIRMED("Confirmed by client", "#2196F3"),
    IN_PROGRESS("Work in progress", "#9C27B0"),
    COMPLETED("Work completed", "#4CAF50"),
    READY_TO_DELIVER("Ready to deliver", "#00BCD4"),
    DELIVERED("Delivered", "#009688"),
    CANCELLED("Cancelled", "#F44336"),
    REFUNDED("Refunded", "#795548");

    private final String displayName;
    private final String colorHex;

    OrderStatus(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getDisplayName() { return displayName; }
    public String getColorHex() { return colorHex; }

    public static OrderStatus fromName(String name) {
        for (OrderStatus status : values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return PENDING_CONFIRMATION;
    }
}
