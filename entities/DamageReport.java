package entities;

import enums.*;

/* - CHANGED: resolveReport() now updates the equipment's availability status to available and changes the equipment's condition to the specified condition after repair.
*  - CHANGED: removed assesSeverity() method since its just the same as getSeverity()
*/
public class DamageReport {
    private String reportID;
    private ReturnRecord returnRecord;
    private Borrower borrower;
    private String description;
    private DamageSeverity severity;
    private String actionTaken;
    private DamageReportStatus status;

    private static int nextReportNumber = 1;

    // Constructor
    public DamageReport(ReturnRecord returnRecord, Borrower borrower, String description, DamageSeverity severity) {
        this.reportID = String.format("DR%02d", nextReportNumber++);
        this.returnRecord = returnRecord;
        this.borrower = borrower;
        this.description = description;
        this.severity = severity;
        this.actionTaken = "";
        this.status = DamageReportStatus.PENDING;
    }

    public void updateActionTaken(String action) {
        this.actionTaken = action;
    }

    // File the damage report
    public boolean fileReport() {
        if (status != DamageReportStatus.PENDING) {
            return false;
        }

        if (returnRecord == null || borrower == null || description == null || description.trim().isEmpty() || severity == null) {
            return false;
        }

        setStatus(DamageReportStatus.FILED);
        return true;
    }

    // Resolve the damage report
    public boolean resolveReport(String actionTaken, EquipmentCondition conditionAfterRepair) {
        if (status != DamageReportStatus.FILED || actionTaken == null || actionTaken.trim().isEmpty() || conditionAfterRepair == null
                || conditionAfterRepair == EquipmentCondition.DAMAGED
                || returnRecord == null) {
            return false;
        }

        BorrowRequest request = returnRecord.getBorrowRequest();
        if (request == null || request.getEquipmentList() == null || request.getEquipmentList().isEmpty()) {
            return false;
        }

        for (Equipment equipment : request.getEquipmentList()) {
            if (equipment == null) return false;
        }

        updateActionTaken(actionTaken.trim());

        for (Equipment equipment : request.getEquipmentList()) {
            equipment.updateCondition(conditionAfterRepair);
            equipment.updateStatus(AvailabilityStatus.AVAILABLE);
        }

        setStatus(DamageReportStatus.RESOLVED);
        return true;
    }

    // GETTERS
    public String getReportID() { return reportID; }
    public ReturnRecord getReturnRecord() { return returnRecord; }
    public Borrower getBorrower() { return borrower; }
    public String getDescription() { return description; }
    public DamageReportStatus getStatus() { return status; }
    public DamageSeverity getSeverity() { return severity; }
    public String getActionTaken() { return actionTaken; }

    // SETTERS
    public void setDescription(String description) { this.description = description; }
    public void setStatus(DamageReportStatus status) { this.status = status; }
}