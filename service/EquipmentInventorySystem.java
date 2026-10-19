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

    // Constructor
    public EquipmentInventorySystem() {
        borrowers = new ArrayList<>();
        custodians = new ArrayList<>();
        equipmentList = new ArrayList<>();
        requests = new ArrayList<>();
        damageReports = new ArrayList<>();
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
        if (equipment != null) {
            equipmentList.add(equipment);
            return equipment;
        }

        return null;
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

    // Get a copy of the equipment inventory
    public ArrayList<Equipment> getEquipmentList() {
        return new ArrayList<>(equipmentList);
    }

    // Find a borrow request by its ID
    public BorrowRequest findBorrowRequestByID(String requestID) {
        if (requestID == null) {
            return null;
        }

        for (BorrowRequest request : requests) {
            if (request.getRequestID().equals(requestID)) {
                return request;
            }
        }

        return null;
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