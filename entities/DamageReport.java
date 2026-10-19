package entities;

import enums.DamageReportStatus;
import enums.DamageSeverity;

public class DamageReport {
    private String reportID;
    private ReturnRecord returnRecord;
    private Borrower borrower;
    private String description;
    private DamageSeverity severity;
    private String actionTaken;
    private DamageReportStatus status;

    private static int nextIDNumber = 1;

    // Constructor
    public DamageReport(ReturnRecord returnRecord, Borrower borrower, String description, DamageSeverity severity) {
        this.reportID = String.format("DR%02d", nextIDNumber++);
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

        if (/*returnRecord == null || borrower == null ||*/ description == null || description.trim().isEmpty() || severity == null) {
            return false;
        }

        setStatus(DamageReportStatus.FILED);
        return true;
    }

    public void assessSeverity() {
        System.out.println("Damage severity: " + severity);
    } // baka alisin to redundant lng/same lng sa getSeverity()

    // Resolve the damage report
    public boolean resolveReport(String actionTaken) {
        if (status == DamageReportStatus.RESOLVED || actionTaken == null || actionTaken.trim().isEmpty()) {
            return false;
        }

        updateActionTaken(actionTaken);
        setStatus(DamageReportStatus.RESOLVED);
        return true;
    }

    // Getters
    public String getReportID() { return reportID; }
    public ReturnRecord getReturnRecord() { return returnRecord; }
    public Borrower getBorrower() { return borrower; }
    public String getDescription() { return description; }
    public DamageReportStatus getStatus() { return status; }
    public DamageSeverity getSeverity() { return severity; }
    public String getActionTaken() { return actionTaken; }

    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setStatus(DamageReportStatus status) { this.status = status; }
}