package entities;

import java.time.LocalDate;
import enums.*;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - NEW FILE: this class was missing from the repo but is used by Custodian, EquipmentInventorySystem and DamageReport
 *  - Fields: returnID (RR01...), borrowRequest, receivedBy, returnDate, conditionAtReturn
 *  - Constructor (request, custodian, condition), isDamaged(), and getters
 * Search for "NEW:" and "CHANGED:" to find each change.
 */
public class ReturnRecord {
    private String returnID;
    private BorrowRequest borrowRequest;
    private Custodian receivedBy;
    private String returnDate;
    private EquipmentCondition conditionAtReturn;

    private static int nextReturnNumber = 1;

    // Constructor
    public ReturnRecord(BorrowRequest borrowRequest, Custodian receivedBy, EquipmentCondition conditionAtReturn) {
        this.returnID = String.format("RR%02d", nextReturnNumber++);
        this.borrowRequest = borrowRequest;
        this.receivedBy = receivedBy;
        this.returnDate = LocalDate.now().toString();
        this.conditionAtReturn = conditionAtReturn;
    }

    // Check whether the returned equipment came back damaged
    public boolean isDamaged() {
        return conditionAtReturn == EquipmentCondition.DAMAGED;
    }

    // GETTERS
    public String getReturnID() { return returnID; }
    public BorrowRequest getBorrowRequest() { return borrowRequest; }
    public Custodian getReceivedBy() { return receivedBy; }
    public String getReturnDate() { return returnDate; }
    public EquipmentCondition getConditionAtReturn() { return conditionAtReturn; }
}
