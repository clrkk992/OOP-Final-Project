package entities;

import enums.*;

public class Equipment {
    private String equipmentID;
    private String name;
    private EquipmentCategory category;
    private String serialNumber;
    private EquipmentCondition condition;
    private AvailabilityStatus availabilityStatus;
    private String location;

    private static int nextEquipmentNumber = 1;

    // Constructor
    public Equipment(String name, EquipmentCategory category, String serialNumber, EquipmentCondition condition, String location) {
        this.equipmentID = String.format("EQ%02d", nextEquipmentNumber++);
        this.name = name;
        this.category = category;
        this.serialNumber = serialNumber;
        this.condition = condition;
        this.location = location;
        this.availabilityStatus = AvailabilityStatus.AVAILABLE;
    }

    // Update the equipment's availability status
    public void updateStatus(AvailabilityStatus newStatus) {
        if (newStatus != null) {
            this.availabilityStatus = newStatus;
        }
    }

    // Check whether the equipment can be borrowed
    public boolean checkAvailability() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE && condition != EquipmentCondition.DAMAGED;
    }

    // Update the equipment's physical condition
    public void updateCondition(EquipmentCondition condition) {
        if (condition != null) {
            this.condition = condition;

            // Damaged equipment should not be available for borrowing
            if (condition == EquipmentCondition.DAMAGED) {
                this.availabilityStatus = AvailabilityStatus.UNAVAILABLE;
            }
        }
    }

    // Getters
    public String getEquipmentID() { return equipmentID; }
    public String getName() { return name; }
    public EquipmentCategory getCategory() { return category; }
    public String getSerialNumber() { return serialNumber; }
    public EquipmentCondition getCondition() { return condition; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }

    // Setter
    public void setLocation(String location) {
        //Checker
        if (location != null && !location.trim().isEmpty()) {
            this.location = location;
        }
    }
}
