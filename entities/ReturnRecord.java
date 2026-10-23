package entities;

import enums.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; //added import for flagLateReturn

// CHANGED: removed generateReturnRecord()

public class ReturnRecord {

    private String returnID;
    private BorrowRequest borrowRequest;
    private String dateReturned;
    private EquipmentCondition conditionAtReturn;
    private Custodian receivedBy;
    private boolean isLate;

    private static int nextReturnID = 1;

    // CONSTURCTOR
    public ReturnRecord(BorrowRequest request, Custodian receivedBy, EquipmentCondition conditionAtReturn) {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate dueDate = LocalDate.parse(borrowRequest.getRequestReturnDate(), formatter);
            //changed from LocalDate dueDate = LocalDate.parse(borrowRequest.getRequestReturnDate()); because it expects different format
            // expects yy/mm/dd instead of mm/dd/yy from borrower

            return returnedDate.isAfter(dueDate);

        } catch (Exception e) {
            return false;
        }
    }

    // Create a damage report for damaged equipment
    public DamageReport triggerDamageReport(Borrower borrower, String description, DamageSeverity severity) {
        return new DamageReport(this, borrower, description, severity);
    }

    // GETTERS
    public String getReturnID() { return returnID;}
    public BorrowRequest getBorrowRequest() { return borrowRequest; }
    public String getReturnDate() { return dateReturned; }
    public EquipmentCondition getConditionAtReturn() { return conditionAtReturn; }
    public Custodian getReceivedBy() { return receivedBy; }
    public boolean isLate() { return isLate; }
}
