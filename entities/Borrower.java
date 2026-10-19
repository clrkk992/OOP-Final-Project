package entities;

import enums.Action;
import java.util.ArrayList;
import service.EquipmentInventorySystem;

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

    // Overrides
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
                || action == Action.VIEW_EQUIPMENTS;
    }

    // GETTERS
    public String getDepartment() { return department; }
    public ArrayList<BorrowRequest> getBorrowingHistory() { return new ArrayList<>(borrowingHistory);}

    // SETTERS
    public void setDepartment(String department) {
        if (department != null && !department.trim().isEmpty()) {
            this.department = department.trim();
        }
    }

    // Creates and records a valid request in both borrower history and the inventory system.
    public BorrowRequest submitBorrowRequest(ArrayList<Equipment> equipmentList, String purpose, String requestedReturnDate, EquipmentInventorySystem system) {
        if (system == null || !checkPermission(Action.SUBMIT_REQUEST) || !isEligible()) {
            return null;
        }

        BorrowRequest request = new BorrowRequest(this, equipmentList, purpose, requestedReturnDate);

        if (!request.validateRequest()) {
            return null;
        }

        system.addBorrowRequest(request);
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
    public ArrayList<Equipment> viewEquipmentList(EquipmentInventorySystem system) {
        if (!checkPermission(Action.VIEW_EQUIPMENTS) || system == null) {
            return new ArrayList<>();
        }
        return system.getEquipmentList();
    }

    // Returns whether this borrower is currently eligible to borrow equipment.
    public boolean isEligible() {
        return !isBlocked();
    }
}