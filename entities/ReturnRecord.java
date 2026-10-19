package entities;

import enums.*;
import java.time.LocalDate;

public class ReturnRecord {

    private String returnID;
    private BorrowRequest borrowRequest;
    private String dateReturned;
    private EquipmentCondition conditionAtReturn;
    private Custodian receivedBy;
    private boolean isLate;

    private static int nextReturnID = 1;

    // Constructor
    public ReturnRecord(BorrowRequest request,
                        Custodian receivedBy,
                        EquipmentCondition conditionAtReturn) {

        this.returnID = String.format("RR%02d", nextReturnID++);
        this.borrowRequest = request;
        this.receivedBy = receivedBy;
        this.dateReturned = LocalDate.now().toString();
        this.conditionAtReturn = conditionAtReturn;
        this.isLate = flagLateReturn();
    }

    // Optional method for recording returns
    public void recordReturn() {
        System.out.println("Return recorded: " + returnID);
    }

    // Check if the returned equipment is damaged
    public boolean isDamaged() {
        return conditionAtReturn == EquipmentCondition.DAMAGED;
    }

    // Check if returned equipment matches a specific condition
    public boolean inspectCondition(EquipmentCondition condition) {
        return conditionAtReturn == condition;
    }

    // Determines whether the return is late
    public boolean checkLateStatus() {
        return flagLateReturn();
    }

    // Check if returned after the requested return date
    public boolean flagLateReturn() {
        try {
            LocalDate returnedDate = LocalDate.parse(dateReturned);
            LocalDate dueDate = LocalDate.parse(borrowRequest.getRequestReturnDate());

            return returnedDate.isAfter(dueDate);

        } catch (Exception e) {
            return false;
        }
    }

    // Create a damage report for damaged equipment
    public DamageReport triggerDamageReport(
            Borrower borrower,
            String description,
            DamageSeverity severity) {

        return new DamageReport(
                this,
                borrower,
                description,
                severity);
    }

    // Generates a printable return record
    public String generateReturnRecord() {
        StringBuilder record = new StringBuilder();

        record.append("=== RETURN RECORD ===\n");
        record.append("Return ID: ").append(getReturnID()).append("\n");
        record.append("Request ID: ").append(getBorrowRequest().getRequestID()).append("\n");
        record.append("Date Returned: ").append(getReturnDate()).append("\n");
        record.append("Equipment Condition: ").append(getConditionAtReturn()).append("\n");
        record.append("Received By: ").append(getReceivedBy().getName()).append("\n");
        record.append("Late Status: ").append(isLate()).append("\n");

        return record.toString();
    }

    // Getters
    public String getReturnID() {
        return returnID;
    }

    public BorrowRequest getBorrowRequest() {
        return borrowRequest;
    }

    public String getReturnDate() {
        return dateReturned;
    }

    public EquipmentCondition getConditionAtReturn() {
        return conditionAtReturn;
    }

    public Custodian getReceivedBy() {
        return receivedBy;
    }

    public boolean isLate() {
        return isLate;
    }
}
