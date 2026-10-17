package entities;

import java.util.*;
import java.time.LocalDate;
import enums.*;

  public class BorrowRequest {

    private String requestID;
    private Borrower borrower;
    private ArrayList<Equipment> equipmentList;
    private String dateRequested;
    private String purpose;
    private String requestReturnDate;
    private BorrowRequestStatus status;

      public BorrowRequest(String requestID, Borrower borrower, ArrayList<Equipment> equipmentList, String purpose, String requestReturnDate) {

      this.requestID = requestID;
      this.borrower = borrower;
      this.equipmentList = equipmentList;
      this.dateRequested = LocalDate.now().toString();
      this.purpose = purpose;
      this.requestReturnDate = requestReturnDate;
      this.status = BorrowRequestStatus.PENDING;
      }

public String getRequestID() {
  return requestID;
}
public ArrayList<Equipment> getEquipmentList() {
  return getEquipmentList;
}
public Borrower getBorrower() {
  return borrower;
  
}public String getDateRequested() {
  return dateRequested;
}
public String getRequestReturnDate() {
  return requestReturnDate;
}
public BorrowRequestStatus getStatus() {
  return status;
}
public String getPurpose() {
  return purpose;
}
    public void updateStatus(BorrowRequestStatus status) {
      this.status = status;
    }

    public boolean approve() {
      if (status == BorrowRequestStatus.PENDING) {
          status = BorrowRequestStatus.APPROVED;
          return true;
      }
      return false;
    }

    public boolean reject(String reason) {
     if (status == BorrowRequestStatus.PENDING) {
          status = BorrowRequestStatus.REJECTED;
          return true;
      }
      return false;
    }
     public boolean cancel() {
     if (status == BorrowRequestStatus.PENDING) {
          status = BorrowRequestStatus.CANCELLED;
          return true;
      }
      return false;
    }

public void release() {
  if (status == BorrowRequestStatus.APPROVED) {
    for(Equipment equipment : equipmentList) {
      equipment.updateStatus(AvailabilityStatus.BORROWED);
    }
    status = BorrowRequestStatus.RELEASED;
  }
}

public void markReturned() {
  if (status == BorrowRequestStatus.RELEASED) {
    for(Equipment equipment : equipmentList) {
      equipment.updateStatus(AvailabilityStatus.AVAILABLE);
    }
    status = BorrowRequestStatus.RETURNED;
  }
}

public boolean validateRequest() {
  return borrower != null
    && equipmentList != null
    && !equipmentList.isEmpty()
    && purpose != null && !purpose.trim().isEmpty()
    && requestReturnDate != null
    && !requestReturnDate.trim().isEmpty();
  }    
}
