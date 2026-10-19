package entities;

import enums.*; // CHANGED: was "import enums.Action;"
import java.util.ArrayList;
import service.EquipmentInventorySystem;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - CHANGED: import enums.Action -> import enums.* (new methods use BorrowRequestStatus and EquipmentCondition)
 *  - CHANGED: checkPermission() now allows Action.RETURN_EQUIPMENT
 *  - NEW: viewAvailableEquipment(system)
 *  - NEW: searchEquipment(keyword, system)
 *  - NEW: viewEquipmentDetails(equipmentID, system)
 *  - NEW: findBorrowRequest(requestID)
 *  - NEW: viewRequestsByStatus(status)
 *  - NEW: viewReturnedHistory()
 *  - NEW: viewReturnableRequests()
 *  - NEW: returnEquipment(requestID, declaredCondition)
 */
public class Borrower extends User {
    private String department;
    private ArrayList<BorrowRequest> borrowingHistory;

    private static int nextBorrowerNumber = 1;

    // Constructor
    public Borrower(String name, String contactNumber, String email, String password, String department) {
        super(String.format("BR%02d", nextBorrowerNumber++), name, contactNumber, email, password);
        this.department = department;
        this.borrowingHistory = new ArrayList<>();
    }

    // OVERRIDES
    @Override
    public String displayRole() {
        return "Borrower";
    }

    @Override
    public boolean checkPermission(Action action) {
        if (isBlocked() || action == null) {
            return false;
        }

        return action == Action.SUBMIT_REQUEST
                || action == Action.CANCEL_REQUEST
                || action == Action.VIEW_HISTORY
                || action == Action.VIEW_EQUIPMENTS
                || action == Action.RETURN_EQUIPMENT; // NEW permission
    }

    // GETTERS
    public String getDepartment() { return department; }
    public ArrayList<BorrowRequest> getBorrowingHistory() { return new ArrayList<>(borrowingHistory);}

    // SETTER
    public void setDepartment(String department) { this.department = department.trim();}

    // Creates and records a valid request in both borrower history and the inventory system.
    public BorrowRequest submitBorrowRequest(ArrayList<Equipment> equipmentList, String purpose, String requestedReturnDate, EquipmentInventorySystem inventorySystem) {
        if (inventorySystem == null || !checkPermission(Action.SUBMIT_REQUEST) || !isEligible()) {
            return null;
        }

        BorrowRequest request = new BorrowRequest(this, equipmentList, purpose, requestedReturnDate);

        if (!request.validateRequest()) {
            return null;
        }

        inventorySystem.addBorrowRequest(request);
        borrowingHistory.add(request);

        return request;
    }

    // Cancels a matching pending request in this borrower's history.
    public boolean cancelBorrowRequest(String requestID) {
        if (!checkPermission(Action.CANCEL_REQUEST) || requestID == null) {
            return false;
        }

        for (BorrowRequest request : borrowingHistory) {
            if (request.getRequestID().equals(requestID)) {
                return request.cancel();
            }
        }
        return false;
    }

    // Returns a copy of the borrowing history if this borrower has permission to view it.
    public ArrayList<BorrowRequest> viewBorrowingHistory() {
        if (!checkPermission(Action.VIEW_HISTORY)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(borrowingHistory);
    }

    //NEW! returns list of equipments from service/system
    public ArrayList<Equipment> viewEquipmentList(EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_EQUIPMENTS) || inventorySystem == null) {
            return new ArrayList<>();
        }
        return inventorySystem.getEquipmentList();
    }

    // NEW: Returns only the equipment that can be borrowed right now.
    public ArrayList<Equipment> viewAvailableEquipment(EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_EQUIPMENTS) || inventorySystem == null) {
            return new ArrayList<>();
        }
        return inventorySystem.getAvailableEquipment();
    }

    // NEW: Searches equipment by keyword (equipment ID or name).
    public ArrayList<Equipment> searchEquipment(String keyword, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_EQUIPMENTS) || inventorySystem == null) {
            return new ArrayList<>();
        }
        return inventorySystem.searchEquipment(keyword);
    }

    // NEW: Returns the details of one equipment by ID, or null if not found.
    public Equipment viewEquipmentDetails(String equipmentID, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_EQUIPMENTS) || inventorySystem == null) {
            return null;
        }
        return inventorySystem.searchEquipmentByID(equipmentID);
    }

    // NEW: Finds one request in this borrower's own history by ID.
    public BorrowRequest findBorrowRequest(String requestID) {
        if (!checkPermission(Action.VIEW_HISTORY) || requestID == null) {
            return null;
        }

        for (BorrowRequest request : borrowingHistory) {
            if (request.getRequestID().equalsIgnoreCase(requestID.trim())) {
                return request;
            }
        }
        return null;
    }

    // NEW: Returns this borrower's requests that match the given status.
    public ArrayList<BorrowRequest> viewRequestsByStatus(BorrowRequestStatus status) {
        ArrayList<BorrowRequest> results = new ArrayList<>();

        if (!checkPermission(Action.VIEW_HISTORY) || status == null) {
            return results;
        }

        for (BorrowRequest request : borrowingHistory) {
            if (request.getStatus() == status) {
                results.add(request);
            }
        }
        return results;
    }

    // NEW: Returns completed borrows (RETURNED) for the borrowing history screen.
    public ArrayList<BorrowRequest> viewReturnedHistory() {
        return viewRequestsByStatus(BorrowRequestStatus.RETURNED);
    }

    // NEW: Returns requests currently borrowed (RELEASED) that can be returned.
    public ArrayList<BorrowRequest> viewReturnableRequests() {
        if (!checkPermission(Action.RETURN_EQUIPMENT)) {
            return new ArrayList<>();
        }
        return viewRequestsByStatus(BorrowRequestStatus.RELEASED);
    }

    // NEW: Files a return notice with the declared condition. The custodian still receives the return.
    public boolean returnEquipment(String requestID, EquipmentCondition declaredCondition) {
        if (!checkPermission(Action.RETURN_EQUIPMENT) || requestID == null || declaredCondition == null) {
            return false;
        }

        BorrowRequest request = findBorrowRequest(requestID);
        if (request == null) {
            return false;
        }
        return request.requestReturn(declaredCondition);
    }

    // Returns whether this borrower is currently eligible to borrow equipment.
    public boolean isEligible() {
        return !isBlocked();
    }
}
