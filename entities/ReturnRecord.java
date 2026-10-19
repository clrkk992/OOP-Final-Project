package entities;

import enums.*;
import java.time.LocalDate;

public class ReturnRecord{
    private String returnID;
    private BorrowRequest request;
    private String dateReturned;
    private EquipmentCondition conditionAtReturn;
    private Custodian receivedBy;
    private boolean isLate;

    private static int nextReturnID = 1;
}

//constructor
public ReturnRecord(BorrowRequest request, Custodian receivedBy, EquipmentCondition conditionAtReturn){
    this.returnID = String.format("RR%02d", nextReturnID++);
    this.request = request;
    this.dateReturned = LocalDate.now().toString()l
    this.conditionAtReturn = conditionAtReturn;
    this.receivedBy = receivedBy;
    this.isLate = checkLateStatus;
    
}

public void recordReturn(){

}

public boolean inspectCondition(EquipmentCondition condition){
    if(getConditionAtReturn() == condition){
        return true;
    }
    else {
        return false;
    }
}

public boolean checkLateStatus(){
   if (flagLateReturn()=true){
       return true; 
   }
   else {
    return false;
   }//feel ko redundant ito, yung flagLateReturn dapat yung checkLateStatus na mismo
}

public DamageReport triggerDamageReport(Borrower borrower, String description, DamageSeverity severity){//Added borrower 
    DamageReport report = DamageReport(this.ReturnRecord, borrower, description, severity);
    return report;
}

public boolean flagLateReturn(){
     if (dateReturned < getRequestReturnDate()){
        return false;
    }
    else{
        return true;
    }
}

public String generateReturnRecord() {
    StringBuilder record = new StringBuilder();

    record.append("=== RETURN RECORD ==="\n);
    record.append("Return ID: ")record.append(getReturnID())record.append("\n");
    record.append("Request: ")record.append(getRequest().requestID)record.append("\n");
    record.append("Date Returned: ")record.append(getDateReurned)record.append("\n");
    record.append("Equipment Condition: ")record.append(getConditionAtReturn)record.append("\n");
    record.append("Received By: ")record.append(getReceivedBy)record.append("\n");
    record.append("Late Status: ")record.append(isLate())record.append("\n");

    return record.toString();
}

//getters
public String getReturnID(){return returnID;}//for Return ID
public BorrowRequest getRequest(){return request;}// for Borrow Request
public conditionAtReturn getConditionAtReturn(){return conditionAtReturn};//for Condition at Return
public Custodian getReceivedBy(){return receivedBy};//for Custodian
public boolean isLate(){return isLate;}//for Late status
public String getDateReurned(){return dateReturned};//for Date Returned - inadd ko to