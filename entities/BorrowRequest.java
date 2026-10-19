package entities;

import java.util.*;
import java.time.LocalDate;
import enums.*;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - NEW fields: dateBorrowed, dateReturned, returnRequested, declaredReturnCondition
 *  - CHANGED: release() now records dateBorrowed
 *  - CHANGED: markReturned() now records dateReturned and returns false if the status did not become RETURNED
 *  - NEW: requestReturn(EquipmentCondition) (borrower return notice, only while RELEASED)
 *  - NEW getters: getDateBorrowed(), getDateReturned(), isReturnRequested(), getDeclaredReturnCondition()
 */
public class BorrowRequest {
    private String requestID;
    private Borrower borrower;
    private ArrayList<Equipment> equipmentList;
    private String dateRequested;
    private String purpose;
    private String requestReturnDate;
    private String rejectionReason; // dinagdag ko to para mastore ung paramater sa reject()
    private BorrowRequestStatus status;
    private String dateBorrowed;   // NEW: set when equipment is released
    private String dateReturned;   // NEW: set when the return is received
    private boolean returnRequested; // NEW: borrower filed a return notice
    private EquipmentCondition declaredReturnCondition; // NEW: condition declared by borrower

    private static int nextRequestNumber = 1;

    public BorrowRequest(Borrower borrower, ArrayList<Equipment> equipmentList, String purpose, String requestReturnDate) {
        this.requestID = String.format("BR%02d", nextRequestNumber++);
        this.borrower = borrower;
        this.equipmentList = equipmentList;
        this.dateRequested = LocalDate.now().toString();
        this.purpose = purpose;
        this.requestReturnDate = requestReturnDate;
        this.status = BorrowRequestStatus.PENDING;
    }

    // Added valid status transitions / Error preventions
    public void updateStatus(BorrowRequestStatus status) {
      if (status == null) { return; }

      switch (this.status) {
          case PENDING:
              if (status == BorrowRequestStatus.APPROVED || status == BorrowRequestStatus.REJECTED || status == BorrowRequestStatus.CANCELLED) {
                  this.status = status;
              }
              break;
          case APPROVED:
              if (status == BorrowRequestStatus.RELEASED) { this.status = status; } break;
          case RELEASED:
              if (status == BorrowRequestStatus.RETURNED) { this.status = status; } break;
          default: // No further status changes allowed
              break;
      }
    }

    // Approve the borrow request
    public boolean approve() {
      if (status == BorrowRequestStatus.PENDING) {
          status = BorrowRequestStatus.APPROVED;
          return true;
      }
      return false;
    }

    // Reject the borrow request
    public boolean reject(String rejectionReason) {
    if (status == BorrowRequestStatus.PENDING && rejectionReason != null && !rejectionReason.trim().isEmpty()) {
          this.rejectionReason = rejectionReason.trim();
          status = BorrowRequestStatus.REJECTED;
          return status == BorrowRequestStatus.REJECTED;
      }
      return false;
    }

    // Cancel the borrow request
    public boolean cancel() {
    if (status == BorrowRequestStatus.PENDING) {
          status = BorrowRequestStatus.CANCELLED;
          return status == BorrowRequestStatus.CANCELLED;
      }
      return false;
    }

    // Release the equipment for borrowing
    public boolean release() {
        if (status != BorrowRequestStatus.APPROVED
                || equipmentList == null
                || equipmentList.isEmpty()) {
            return false;
        }

        for (Equipment equipment : equipmentList) {
            if (equipment == null
                    || !equipment.checkAvailability()) {
                return false;
            }
        }

        for (Equipment equipment : equipmentList) {
            equipment.updateStatus(AvailabilityStatus.BORROWED);
        }

        status = BorrowRequestStatus.RELEASED;
        dateBorrowed = LocalDate.now().toString(); // CHANGED: records the borrow date
        return true;
    }

    // Mark the borrowed equipment as returned
    public boolean markReturned() {
        if (status != BorrowRequestStatus.RELEASED) {
            return false;
        }

        for (Equipment equipment : equipmentList) {
            if (equipment == null) {
                return false;
            }
        }

        for (Equipment equipment : equipmentList) {
            if (equipment.getCondition() == EquipmentCondition.DAMAGED) {
                equipment.updateStatus(AvailabilityStatus.UNAVAILABLE);
            } else {
                equipment.updateStatus(AvailabilityStatus.AVAILABLE);
            }
        }

        updateStatus(BorrowRequestStatus.RETURNED);
        // CHANGED: records the return date, and only returns true when the status really became RETURNED
        if (status == BorrowRequestStatus.RETURNED) {
            dateReturned = LocalDate.now().toString();
            return true;
        }
        return false;
    }


    // NEW: Borrower files a return notice; the custodian still receives the return
    public boolean requestReturn(EquipmentCondition declaredCondition) {
        if (status != BorrowRequestStatus.RELEASED || declaredCondition == null) {
            return false;
        }
        this.returnRequested = true;
        this.declaredReturnCondition = declaredCondition;
        return true;
    }

    public boolean validateRequest() {
      return borrower != null
        && equipmentList != null
        && !equipmentList.isEmpty()
        && purpose != null && !purpose.trim().isEmpty()
        && requestReturnDate != null
        && !requestReturnDate.trim().isEmpty();
    }

    // Getters
    public String getRequestID() { return requestID; }
    public ArrayList<Equipment> getEquipmentList() { return equipmentList; }
    public Borrower getBorrower() { return borrower; }
    public String getDateRequested() { return dateRequested; }
    public String getRequestReturnDate() { return requestReturnDate; }
    public BorrowRequestStatus getStatus() { return status; }
    public String getPurpose() { return purpose; }
    public String getRejectionReason() { return rejectionReason; }
    // NEW getters (borrowing history and return notice)
    public String getDateBorrowed() { return dateBorrowed; }
    public String getDateReturned() { return dateReturned; }
    public boolean isReturnRequested() { return returnRequested; }
    public EquipmentCondition getDeclaredReturnCondition() { return declaredReturnCondition; }
}
