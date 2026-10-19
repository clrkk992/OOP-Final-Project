package service;

import java.util.ArrayList;
import entities.*;
import enums.*;

public class EquipmentInventorySystem {
    private ArrayList<Borrower> borrowers;
    private ArrayList<Custodian> custodians;
    private ArrayList<Equipment> equipmentList;
    private ArrayList<BorrowRequest> requests;
    private ArrayList<DamageReport> damageReports;
    private ArrayList<Equipment> removedEquipment;
    private ArrayList<ReturnRecord> returnRecords;

    // Constructor
    public EquipmentInventorySystem() {
        borrowers = new ArrayList<>();
        custodians = new ArrayList<>();
        equipmentList = new ArrayList<>();
        requests = new ArrayList<>();
        damageReports = new ArrayList<>();
        removedEquipment = new ArrayList<>();
        returnRecords = new ArrayList<>();
    }

    // Register a borrower
    public void registerBorrower(Borrower borrower) {
        if (borrower != null) {
            borrowers.add(borrower);
        }
    }

    // Register a custodian
    public void registerCustodian(Custodian custodian) {
        if (custodian != null) {
            custodians.add(custodian);
        }
    }

    // Add equipment to the inventory
    public Equipment addEquipment(Equipment equipment) {
        if (equipment == null || equipmentList.contains(equipment)) {
            return null;
        }

        equipmentList.add(equipment);
        return equipment;
    }

    // Add a borrow request
    public void addBorrowRequest(BorrowRequest request) {
        if (request != null) {
            requests.add(request);
        }
    }

    // Add a damage report
    public void addDamageReport(DamageReport report) {
        if (report != null) {
            damageReports.add(report);
        }
    }

    // Search equipment by keyword
    public ArrayList<Equipment> searchEquipment(String keyword) {
        ArrayList<Equipment> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        keyword = keyword.trim().toLowerCase();

        for (Equipment equipment : equipmentList) {
            if (equipment.getEquipmentID().toLowerCase().contains(keyword)
                    || equipment.getName().toLowerCase().contains(keyword)
                    || equipment.getCategory().toString()
                            .toLowerCase().contains(keyword)
                    || equipment.getSerialNumber().toLowerCase()
                            .contains(keyword)) {

                results.add(equipment);
            }
        }

        return results;
    }

    // Filter borrow requests by status
    public ArrayList<BorrowRequest> filterRequestsByStatus(BorrowRequestStatus status) {
        ArrayList<BorrowRequest> results = new ArrayList<>();

        if (status == null) {
            return results;
        }

        for (BorrowRequest request : requests) {
            if (request.getStatus() == status) {
                results.add(request);
            }
        }

        return results;
    }

    // Authenticate a registered user
    public User authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        for (Borrower borrower : borrowers) {
            if (borrower.getEmail().equalsIgnoreCase(email)
                    && borrower.getPassword().equals(password)
                    && !borrower.isBlocked()) {
                return borrower;
            }
        }

        for (Custodian custodian : custodians) {
            if (custodian.getEmail().equalsIgnoreCase(email)
                    && custodian.getPassword().equals(password)
                    && !custodian.isBlocked()) {
                return custodian;
            }
        }

        return null;
    }

    // GETTERS 

    // GET ACTIVE EQUIPMENT LIST
    public ArrayList<Equipment> getEquipmentList() { return new ArrayList<>(equipmentList); }

    // GET REMOVED EQUIPMENT HISTORY
    public ArrayList<Equipment> getRemovedEquipment() { return new ArrayList<>(removedEquipment); }

    public ArrayList<ReturnRecord> getReturnRecords() { return new ArrayList<>(returnRecords); }
    public ArrayList<DamageReport> getDamageReports() { return new ArrayList<>(damageReports); }

    // SEARCH BORROW REQUEST BY ID
    public BorrowRequest searchRequest(String requestID) {
        if (requestID == null || requestID.trim().isEmpty()) {
            return null;
        }

        for (BorrowRequest request : requests) {
            if (request != null
                    && requestID.equals(request.getRequestID())) {
                return request;
            }
        }

        return null;
    }

    // UPDATE EQUIPMENT DETAILS
    public boolean updateEquipment(Equipment equipment, String name, EquipmentCategory category, String serialNumber, EquipmentCondition condition, String location) {
        if (equipment == null
                || !equipmentList.contains(equipment)
                || name == null || name.trim().isEmpty()
                || category == null
                || serialNumber == null || serialNumber.trim().isEmpty()
                || condition == null
                || location == null || location.trim().isEmpty()) {
            return false;
        }

        equipment.setName(name.trim());
        equipment.setCategory(category);
        equipment.setSerialNumber(serialNumber.trim());
        equipment.updateCondition(condition);
        equipment.setLocation(location.trim());

        return true;
    }

    // REMOVE EQUIPMENT AND SAVE IT IN REMOVAL HISTORY
    public boolean removeEquipment(Equipment equipment, Custodian custodian) {
        if (equipment == null || custodian == null
            || !custodian.checkPermission(Action.REMOVE_EQUIPMENT)
            || !equipmentList.contains(equipment)
            || equipment.getAvailabilityStatus()
                    != AvailabilityStatus.AVAILABLE) {
            return false;
        }

        equipment.updateStatus(AvailabilityStatus.UNAVAILABLE);

        if (equipmentList.remove(equipment)) {
            removedEquipment.add(equipment);
            return true;
        }

        return false;
    }

    // RECORD A RECEIVED RETURN
    public boolean recordReturn(ReturnRecord record) {
        if (record == null || returnRecords.contains(record)) {
            return false;
        }

        returnRecords.add(record);
        return true;
    }

    // Generate the inventory summary
    public String generateSummaryReport() {
        StringBuilder report = new StringBuilder();

        report.append("===== INVENTORY SUMMARY =====\n");
        report.append("Total Borrowers: ").append(borrowers.size()).append("\n");
        report.append("Total Custodians: ").append(custodians.size()).append("\n");
        report.append("Total Equipment: ").append(equipmentList.size()).append("\n");
        report.append("Total Borrow Requests: ").append(requests.size()).append("\n");
        report.append("Total Damage Reports: ").append(damageReports.size()).append("\n");

        return report.toString(); // returns the formatted string to be printed by the console class
    }
}