package entities;

import enums.*;
import java.util.ArrayList;
import service.EquipmentInventorySystem;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - CHANGED: receiveReturn() now applies the checked condition to the returned equipment and calls request.markReturned() after the record is saved (before, it only saved the record)
 *  - NEW: changeEquipmentAvailability(Equipment, AvailabilityStatus) custodian can change availability of equipment if not currently borrowed
 *  - CHANGED: removed assignedStorageArea since it is only printed in the report and not used in any logic
 */
public class Custodian extends User {
    private static int nextCustodianNumber = 1;

    // CONSTRUCTOR
    public Custodian(String name, String email, String password) {
        super(String.format("CU%02d", nextCustodianNumber++), name, email, password);
    }

    // OVERRIDES
    @Override
    public String displayRole() {
        return "Custodian";
    }

    @Override
    public boolean checkPermission(Action action) {
        if (action == null) {
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

    //NEW! changes the availability of the equipment if this custodian has permission and the equipment is not currently borrowed
    public boolean changeEquipmentAvailability(Equipment equipment, AvailabilityStatus newStatus) {
        if (!checkPermission(Action.UPDATE_EQUIPMENT)
                || equipment == null
                || newStatus == null
                || equipment.getAvailabilityStatus() == AvailabilityStatus.BORROWED) {
            return false;
        }

        if (newStatus == AvailabilityStatus.AVAILABLE && equipment.getCondition() == EquipmentCondition.DAMAGED) {
            return false;
        }

        equipment.updateStatus(newStatus);
        return true;
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
                || request.getStatus() != BorrowRequestStatus.RETURN_REQUESTED || !request.isReturnRequested()) { //added || !request.isReturnRequested
            return null;
        }

        ReturnRecord record = new ReturnRecord(request, this, conditionAtReturn);
        // Suppose to record each received returns
        if (!inventorySystem.recordReturn(record)) {
            return null;
        }

        // CHANGED: Apply the checked condition to the returned equipment, then close the request
        // (before, receiveReturn() only saved the record, so the request never became RETURNED)
        for (Equipment equipment : request.getEquipmentList()) {
            if (equipment != null) {
                equipment.updateCondition(conditionAtReturn);
            }
        }
        request.markReturned();

        return record;
    }

    // Resolves the matching damage report if this custodian has permission.
    public boolean resolveDamageReport(String reportID, String actionTaken, EquipmentCondition conditionAfterRepair, EquipmentInventorySystem inventorySystem) {
        if (!checkPermission(Action.RESOLVE_DAMAGE) || inventorySystem == null || reportID == null || reportID.trim().isEmpty()) {
            return false;
        }

        for (DamageReport report : inventorySystem.getDamageReports()) {
            if (report.getReportID().equals(reportID)) {
                return report.resolveReport(actionTaken, conditionAfterRepair);
            }
        }
        return false;
    }
}
