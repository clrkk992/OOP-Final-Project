package service;

import entities.*;
import enums.*;
import java.util.ArrayList;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - CHANGED: registerBorrower() and registerCustodian() now return boolean and reject duplicate emails
 *  - NEW: emailExists(email)
 *  - NEW: searchEquipmentByID(equipmentID)
 *  - NEW: getAvailableEquipment()
 *  - CHANGED: searchEquipment() now matches equipment ID and name only
 * Search for "NEW:" and "CHANGED:" to find each change.
 */
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

    // CHANGED: now returns boolean and rejects duplicate emails (was void)
    // Register a borrower
    public boolean registerBorrower(Borrower borrower) {
        if (borrower == null || emailExists(borrower.getEmail())) {
            return false;
        }
        borrowers.add(borrower);
        return true;
    }

    // CHANGED: now returns boolean and rejects duplicate emails (was void)
    // Register a custodian
    public boolean registerCustodian(Custodian custodian) {
        if (custodian == null || emailExists(custodian.getEmail())) {
            return false;
        }
        custodians.add(custodian);
        return true;
    }

    // NEW: Check if an email is already used by any registered user
    public boolean emailExists(String email) {
        if (email == null) {
            return false;
        }
        for (Borrower borrower : borrowers) {
            if (borrower.getEmail().equalsIgnoreCase(email.trim())) { return true; }
        }
        for (Custodian custodian : custodians) {
            if (custodian.getEmail().equalsIgnoreCase(email.trim())) { return true; }
        }
        return false;
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

    // CHANGED: now matches equipment ID and name only (before: also category and serial number)
    // Search equipment by keyword (matches ID or name only)
    public ArrayList<Equipment> searchEquipment(String keyword) {
        ArrayList<Equipment> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        keyword = keyword.trim().toLowerCase();

        for (Equipment equipment : equipmentList) {
            if (equipment.getEquipmentID().toLowerCase().contains(keyword)
                    || equipment.getName().toLowerCase().contains(keyword)) {

                results.add(equipment);
            }
        }

        return results;
    }

    // NEW: Find one equipment by its ID (active inventory only)
    public Equipment searchEquipmentByID(String equipmentID) {
        if (equipmentID == null || equipmentID.trim().isEmpty()) {
            return null;
        }

        for (Equipment equipment : equipmentList) {
            if (equipment.getEquipmentID().equalsIgnoreCase(equipmentID.trim())) {
                return equipment;
            }
        }

        return null;
    }

    // NEW: Get only the equipment that can be borrowed right now
    public ArrayList<Equipment> getAvailableEquipment() {
        ArrayList<Equipment> results = new ArrayList<>();

        for (Equipment equipment : equipmentList) {
            if (equipment.checkAvailability()) {
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
    public ArrayList<Borrower> getBorrowers() { return new ArrayList<>(borrowers); }
    public ArrayList<Custodian> getCustodians() { return new ArrayList<>(custodians); }

    // SEARCH BORROW REQUEST BY ID
    public BorrowRequest searchRequest(String requestID) {
        if (requestID == null || requestID.trim().isEmpty()) {
            return null;
        }

        for (BorrowRequest request : requests) {
            if (request != null && requestID.equalsIgnoreCase(request.getRequestID())) { //added equalsIgnoreCase
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
}