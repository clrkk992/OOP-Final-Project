package entities;

import java.util.ArrayList;
import enums.*;
import service.EquipmentInventorySystem;

public class Custodian extends User {

    private String assignedStorageArea;
    private static int nextCustodianNumber = 1;

    // CONSTRUCTOR
    public Custodian(String name, String contactNumber, String email, String password, String assignedStorageArea) {
        super(String.format("CU%02d", nextCustodianNumber++), name, contactNumber, email, password);
        this.assignedStorageArea = assignedStorageArea;
    }

    // OVERRIDES
    @Override
    public String displayRole() {
        return "Custodian";
    }

    @Override
    public boolean checkPermission(Action action) {
        if (isBlocked() || action == null) {
            return false;
        }

        return action == Action.APPROVE_REQUEST
                || action == Action.REJECT_REQUEST
                || action == Action.RELEASE_EQUIPMENT
                || action == Action.RECEIVE_RETURN
                || action == Action.ADD_EQUIPMENT
                || action == Action.UPDATE_EQUIPMENT
                || action == Action.REMOVE_EQUIPMENT
                || action == Action.RESOLVE_DAMAGE
                || action == Action.VIEW_REQUESTS
                || action == Action.VIEW_EQUIPMENTS;
    }

    // GETTER / Returns the storage area assigned to this custodian.
    public String getAssignedStorageArea() { return assignedStorageArea; }

    // Returns requests matching the status if this custodian may view requests.
    public ArrayList<BorrowRequest> viewBorrowRequests(BorrowRequestStatus status, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_REQUESTS) || inventorySystem == null) {
            return new ArrayList<>();
        }
        return inventorySystem.filterRequestsByStatus(status);
    }

    //NEW! method to search specific request available only to custodian
    public BorrowRequest searchRequest(String requestID, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.VIEW_REQUESTS) || inventorySystem == null || requestID == null || requestID.trim().isEmpty()) {
            return null;
        }

        return inventorySystem.searchRequest(requestID);
    }

    // Creates an equipment and returns it for service
    public Equipment addEquipment(String name, EquipmentCategory category, String serialNumber, EquipmentCondition condition, String location, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.ADD_EQUIPMENT) || inventorySystem == null) {
            return null;
        }

        Equipment item = new Equipment(name, category, serialNumber, condition, location);
        return inventorySystem.addEquipment(item);
    }

    // NOTE: old name (updateEquipment) only edits the condition and may be used in resolve damage reports
    public boolean updateCondition(Equipment equipment, EquipmentCondition newCondition) {
        if (!checkPermission(Action.UPDATE_EQUIPMENT) || equipment == null || newCondition == null) {
            return false;
        }
        
        equipment.updateCondition(newCondition);
        return true;
    }

    //NEW! updates equipment details
    public boolean updateEquipment(Equipment equipment, String name, EquipmentCategory category, String serialNumber, EquipmentCondition condition, String location, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.UPDATE_EQUIPMENT) || equipment == null || inventorySystem == null) {
            return false;
        }
        return inventorySystem.updateEquipment(equipment, name, category, serialNumber, condition, location);
    }

    // Removes equipment from active inventory and saves it in removal history. (ito yung snapshot)
    public boolean removeEquipment(Equipment equipment, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.REMOVE_EQUIPMENT) || equipment == null || inventorySystem == null) {
            return false;
        }
        return inventorySystem.removeEquipment(equipment, this);
    }

    // Approves the request if this custodian has permission and the request allows it.
    public boolean approveRequest(BorrowRequest request) {
        if (!checkPermission(Action.APPROVE_REQUEST) || request == null) {
            return false;
        }
        return request.approve();
    }

    // Rejects the request with a reason if this custodian has permission.
    public boolean rejectRequest(BorrowRequest request, String reason) {
        if (!checkPermission(Action.REJECT_REQUEST) || request == null) {
            return false;
        }
        return request.reject(reason);
    }

    // Releases equipment for an approved request if this custodian has permission.
    public boolean releaseEquipment(BorrowRequest request) {
        if (!checkPermission(Action.RELEASE_EQUIPMENT) || request == null) {
            return false;
        }
        return request.release();
    }

    // Records a return in the inventory system and identifies this custodian as receiver.
    public ReturnRecord receiveReturn(BorrowRequest request, EquipmentCondition conditionAtReturn, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.RECEIVE_RETURN) || request == null || conditionAtReturn == null || inventorySystem == null 
                || request.getStatus() != BorrowRequestStatus.RELEASED) {
            return null;
        }

        ReturnRecord record = new ReturnRecord(request, this, conditionAtReturn);
        // Suppose to record each received returns
        if (inventorySystem.recordReturn(record)) {
            return record;
        }
        return null;
    }

    // Resolves the matching damage report if this custodian has permission.
    public boolean resolveDamageReport(String reportID, String actionTaken, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.RESOLVE_DAMAGE) || inventorySystem == null || reportID == null || reportID.trim().isEmpty()) {
            return false;
        }

        for (DamageReport report : inventorySystem.getDamageReports()) {
            if (report.getReportID().equals(reportID)) {
                return report.resolveReport(actionTaken);
            }
        }
        return false;
    }
}