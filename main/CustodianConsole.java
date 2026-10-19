
package main;

import java.util.ArrayList;
import java.util.Scanner;
import entities.*;
import enums.*;
import service.EquipmentInventorySystem;

public class CustodianConsole extends Console {
    private final Custodian custodian;
    private final EquipmentInventorySystem inventorySystem;

    public CustodianConsole(Scanner input, EquipmentInventorySystem inventorySystem, Custodian custodian) {
        super(input);
        this.inventorySystem = inventorySystem;
        this.custodian = custodian;
    }

    @Override
    protected void showConsole() {
        showHeader("CUSTODIAN DASHBOARD");
        showMessage("Welcome, " + custodian.getName());
        showMessage("Custodian ID: " + custodian.getUserID());
        showMessage("Storage Area: " + custodian.getAssignedStorageArea());
        System.out.println();
        System.out.println("[1] Equipment Management");
        System.out.println("[2] Borrowing and Returns");
        System.out.println("[3] Damage Reports");
        System.out.println("[4] Reports and Summary");
        System.out.println("[0] Logout");
        System.out.print("Enter your choice: ");
    }

    @Override
    protected void executeChoice(int choice) {
        switch (choice) {
            case 1: equipmentManagementMenu(); break;
            case 2: borrowingAndReturnsMenu(); break;
            case 3: damageReportsMenu(); break;
            case 4: reportsAndSummaryMenu(); break;
            case 0: showMessage("Logged out. Goodbye, " + custodian.getName() + "!"); break;
            default: showMessage("Invalid choice.");
        }
    }

    // EQUIPMENT MANAGEMENT
    private void equipmentManagementMenu() {
        boolean back = false;
        while (!back) {
            showHeader("EQUIPMENT MANAGEMENT");
            System.out.println("[1] Browse Equipment");
            System.out.println("[2] Add Equipment");
            System.out.println("[3] Manage Equipment");
            System.out.println("[0] Return to Dashboard");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 3)) {
                case 1: browseEquipmentMenu(); break;
                case 2: addEquipment(); break;
                case 3: manageEquipment(); break;
                case 0: back = true; break;
            }
        }
    }

    private void browseEquipmentMenu() {
        boolean back = false;
        while (!back) {
            showHeader("BROWSE EQUIPMENT");
            System.out.println("[1] View All Equipment");
            System.out.println("[2] Search by Equipment ID");
            System.out.println("[3] Filter Equipment by Category");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 3)) {
                case 1: viewAllEquipment(); break;
                case 2: searchEquipment(); break;
                case 3: filterEquipment(); break;
                case 0: back = true; break;
            }
        }
    }

    private void viewAllEquipment() {
        ArrayList<Equipment> equipment = inventorySystem.getEquipmentList();
        showHeader("ALL EQUIPMENT");
        if (equipment.isEmpty()) showMessage("No equipment records found.");
        else for (Equipment item : equipment) printEquipmentDetails(item);
        pause();
    }

    private void searchEquipment() {
        showHeader("SEARCH EQUIPMENT");
        System.out.print("Enter Equipment ID: ");
        Equipment equipment = inventorySystem.searchEquipmentByID(readLine());
        if (equipment == null) showMessage("Equipment not found.");
        else printEquipmentDetails(equipment);
        pause();
    }

    private void filterEquipment() {
        showHeader("FILTER EQUIPMENT");
        ArrayList<Equipment> matches = new ArrayList<>();
        EquipmentCategory selected = selectCategory("Select Category:");
        for (Equipment item : inventorySystem.getEquipmentList()) {
            if (item.getCategory() == selected) matches.add(item);
        }
        displayEquipmentList(matches, "FILTERED EQUIPMENT");
        pause();
    }

    private void displayEquipmentList(ArrayList<Equipment> equipment, String title) {
        showHeader(title);
        if (equipment.isEmpty()) {
            showMessage("No matching equipment found.");
            return;
        }
        for (Equipment item : equipment) printEquipmentDetails(item);
    }

    private void addEquipment() {
        showHeader("ADD EQUIPMENT");
        System.out.print("Enter Equipment Name: ");
        String name = readLine();
        EquipmentCategory category = selectCategory("Select Category:");
        System.out.print("Enter Serial Number: ");
        String serial = readLine();
        EquipmentCondition condition = selectCondition("Select Condition:");
        System.out.print("Enter Location: ");
        String location = readLine();

        showHeader("EQUIPMENT SUMMARY");
        showField("Name", name);
        showField("Category", category);
        showField("Serial Number", serial);
        showField("Condition", condition);
        showField("Availability", AvailabilityStatus.AVAILABLE);
        showField("Location", location);

        if (!confirmAction("Confirm equipment addition?")) {
            showMessage("Equipment addition cancelled.");
            pause();
            return;
        }

        Equipment added = custodian.addEquipment(name, category, serial, condition, location, inventorySystem);
        if (added != null) {
            showMessage("Equipment added successfully!");
            showField("Generated Equipment ID", added.getEquipmentID());
        } else showMessage("Failed to add equipment.");
        pause();
    }

    private void manageEquipment() {
        showHeader("MANAGE EQUIPMENT");
        System.out.print("Enter Equipment ID: ");
        Equipment equipment = inventorySystem.searchEquipmentByID(readLine());
        if (equipment == null) {
            showMessage("Equipment not found.");
            pause();
            return;
        }
        printEquipmentDetails(equipment);
        System.out.println("[1] Update Details");
        System.out.println("[2] Remove Equipment");
        System.out.println("[0] Back");
        System.out.print("Enter your choice: ");
        switch (readInt(0, 2)) {
            case 1: updateEquipmentDetails(equipment); break;
            case 2: removeEquipment(equipment); break;
            case 0: break;
        }
    }

    private void updateEquipmentDetails(Equipment equipment) {
        showHeader("UPDATE EQUIPMENT");
        System.out.print("Enter New Name: ");
        String name = readLine();
        EquipmentCategory category = selectCategory("Select New Category:");
        System.out.print("Enter New Serial Number: ");
        String serial = readLine();
        EquipmentCondition condition = selectCondition("Select New Condition:");
        System.out.print("Enter New Location: ");
        String location = readLine();

        if (!confirmAction("Confirm equipment update")) {
            showMessage("Equipment update cancelled.");
            pause();
            return;
        }

        boolean updated = custodian.updateEquipment(equipment, name, category, serial, condition, location, inventorySystem);
        showMessage(updated ? "Equipment updated successfully." : "Failed to update equipment.");
        pause();
    }

    private void removeEquipment(Equipment equipment) {
        showHeader("REMOVE EQUIPMENT");
        printEquipmentDetails(equipment);
        if (!confirmAction("Remove this equipment")) showMessage("Removal cancelled.");
        else if (custodian.removeEquipment(equipment, inventorySystem)) showMessage("Equipment removed successfully.");
        else showMessage("Failed to remove equipment. Check its status and your permissions.");
        pause();
    }

    // BORROWING AND RETURNS
    private void borrowingAndReturnsMenu() {
        boolean back = false;
        while (!back) {
            showHeader("BORROWING AND RETURNS");
            System.out.println("[1] Browse Borrowing Requests");
            System.out.println("[2] Process Borrowing Request");
            System.out.println("[3] Release Equipment");
            System.out.println("[4] Receive Returned Equipment");
            System.out.println("[5] View Return Records");
            System.out.println("[0] Return to Dashboard");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 5)) {
                case 1: browseRequestsMenu(); break;
                case 2: processRequest(); break;
                case 3: releaseEquipment(); break;
                case 4: receiveReturn(); break;
                case 5: viewReturnRecords(); break;
                case 0: back = true; break;
            }
        }
    }

    private void browseRequestsMenu() {
        boolean back = false;
        while (!back) {
            showHeader("BORROWING REQUESTS");
            System.out.println("[1] View All Requests");
            System.out.println("[2] Search by Request ID");
            System.out.println("[3] Filter by Status");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 3)) {
                case 1: viewAllRequests(); break;
                case 2: searchRequest(); break;
                case 3: filterRequestsByStatus(); break;
                case 0: back = true; break;
            }
        }
    }

    private void viewAllRequests() {
        ArrayList<BorrowRequest> requests = getAllRequests();
        showHeader("ALL BORROWING REQUESTS");
        if (requests.isEmpty()) showMessage("No borrowing requests found.");
        else for (BorrowRequest request : requests) printRequestDetails(request);
        pause();
    }

    private void searchRequest() {
        showHeader("SEARCH BORROWING REQUEST");
        System.out.print("Enter Request ID: ");
        BorrowRequest request = custodian.searchRequest(readLine(), inventorySystem);
        if (request == null) showMessage("Request not found.");
        else printRequestDetails(request);
        pause();
    }

    private void filterRequestsByStatus() {
        showHeader("FILTER BORROWING REQUESTS");
        BorrowRequestStatus[] statuses = BorrowRequestStatus.values();
        for (int i = 0; i < statuses.length; i++) System.out.printf("[%d] %s%n", i + 1, statuses[i]);
        System.out.println("[0] Back");
        System.out.print("Enter your choice: ");
        int choice = readInt(0, statuses.length);
        if (choice == 0) return;

        BorrowRequestStatus status = statuses[choice - 1];
        ArrayList<BorrowRequest> matches = custodian.viewBorrowRequests(status, inventorySystem);
        showHeader("REQUESTS: " + status);
        if (matches.isEmpty()) showMessage("No requests found with this status.");
        else for (BorrowRequest request : matches) printRequestDetails(request);
        pause();
    }

    private void processRequest() {
        showHeader("PROCESS BORROWING REQUEST");
        System.out.print("Enter Request ID: ");
        BorrowRequest request = custodian.searchRequest(readLine(), inventorySystem);
        if (request == null || request.getStatus() != BorrowRequestStatus.PENDING) {
            showMessage("No pending borrowing request found.");
            pause();
            return;
        }

        printRequestDetails(request);
        System.out.println("[1] Approve Request");
        System.out.println("[2] Reject Request");
        System.out.println("[0] Back");
        System.out.print("Enter your choice: ");
        int choice = readInt(0, 2);

        if (choice == 1) {
            if (!confirmAction("Approve this request")) showMessage("Approval cancelled.");
            else if (custodian.approveRequest(request)) showMessage("Request approved successfully.");
            else showMessage("Failed to approve request.");
        } else if (choice == 2) {
            System.out.print("Enter Reason for Rejection: ");
            String reason = readLine();
            if (!confirmAction("Reject this request")) showMessage("Rejection cancelled.");
            else if (custodian.rejectRequest(request, reason)) showMessage("Request rejected successfully.");
            else showMessage("Failed to reject request.");
        }
        pause();
    }

    private void releaseEquipment() {
        showHeader("RELEASE EQUIPMENT");
        System.out.print("Enter Request ID: ");
        BorrowRequest request = custodian.searchRequest(readLine(), inventorySystem);
        if (request == null || request.getStatus() != BorrowRequestStatus.APPROVED) {
            showMessage("No approved request found.");
            pause();
            return;
        }

        printRequestDetails(request);
        if (!confirmAction("Confirm equipment release")) showMessage("Release cancelled.");
        else if (custodian.releaseEquipment(request)) showMessage("Equipment released successfully.");
        else showMessage("Failed to release equipment. Check availability.");
        pause();
    }

    private BorrowRequest findReleasedRequest(String id) {
        ArrayList<BorrowRequest> released = custodian.viewBorrowRequests(BorrowRequestStatus.RELEASED, inventorySystem);
        for (BorrowRequest request : released) {
            if (request.getRequestID().equalsIgnoreCase(id)) return request;
            for (Equipment equipment : request.getEquipmentList()) {
                if (equipment.getEquipmentID().equalsIgnoreCase(id)) return request;
            }
        }
        return null;
    }

    private void receiveReturn() {
        showHeader("RECEIVE EQUIPMENT RETURN");
        System.out.print("Enter Equipment ID or Request ID: ");
        BorrowRequest request = findReleasedRequest(readLine());
        if (request == null) {
            showMessage("No released request matched that ID.");
            pause();
            return;
        }

        printRequestDetails(request);
        EquipmentCondition condition = selectCondition("Select Condition Upon Return:");
        String description = null;
        DamageSeverity severity = null;

        if (condition == EquipmentCondition.DAMAGED) {
            System.out.print("Enter Damage Description: ");
            description = readLine();
            severity = selectSeverity("Select Damage Severity:");
        }

        if (!confirmAction("Confirm equipment return?")) {
            showMessage("Return cancelled.");
            pause();
            return;
        }

        ReturnRecord record = custodian.receiveReturn(request, condition, inventorySystem);
        if (record == null) {
            showMessage("Failed to record return.");
            pause();
            return;
        }

        showMessage("Equipment return recorded successfully.");
        showField("Return Record ID", record.getReturnID());
        showField("Request ID", request.getRequestID());
        showField("Condition", record.getConditionAtReturn());

        if (record.isDamaged()) {
            DamageReport report = record.triggerDamageReport(request.getBorrower(), description, severity);
            if (report != null && report.fileReport()) {
                inventorySystem.addDamageReport(report);
                showMessage("Damage report filed successfully.");
                showField("Report ID", report.getReportID());
            } else showMessage("Warning: Failed to file damage report.");
        }
        pause();
    }

    private void viewReturnRecords() {
        ArrayList<ReturnRecord> records = inventorySystem.getReturnRecords();
        showHeader("RETURN RECORDS");
        if (records.isEmpty()) {
            showMessage("No return records found.");
            pause();
            return;
        }

        for (ReturnRecord record : records) {
            showSeparator();
            showField("Return ID", record.getReturnID());
            showField("Request ID", record.getBorrowRequest().getRequestID());
            showField("Equipment", getEquipmentNames(record.getBorrowRequest()));
            showField("Condition", record.getConditionAtReturn());
        }
        showSeparator();

        while (true) {
            System.out.println("[1] View Detailed Return Record");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            if (readInt(0, 1) == 0) return;
            System.out.print("Enter Return Record ID: ");
            ReturnRecord found = findReturnRecord(records, readLine());
            if (found == null) showMessage("Return record not found.");
            else printReturnDetails(found);
            pause();
        }
    }

    private ReturnRecord findReturnRecord(ArrayList<ReturnRecord> records, String id) {
        for (ReturnRecord record : records) {
            if (record.getReturnID().equalsIgnoreCase(id)) return record;
        }
        return null;
    }

    // DAMAGE REPORTS
    private void damageReportsMenu() {
        boolean back = false;
        while (!back) {
            showHeader("DAMAGE REPORTS");
            System.out.println("[1] Browse Damage Reports");
            System.out.println("[2] Manage Damage Report");
            System.out.println("[0] Return to Dashboard");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 2)) {
                case 1: browseDamageReportsMenu(); break;
                case 2: manageDamageReportMenu(); break;
                case 0: back = true; break;
            }
        }
    }

    private void browseDamageReportsMenu() {
        boolean back = false;
        while (!back) {
            showHeader("BROWSE DAMAGE REPORTS");
            System.out.println("[1] View All Reports");
            System.out.println("[2] Search by Report ID");
            System.out.println("[3] Filter by Severity");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 3)) {
                case 1: viewAllDamageReports(); break;
                case 2: searchDamageReport(); break;
                case 3: filterDamageReportsBySeverity(); break;
                case 0: back = true; break;
            }
        }
    }

    private void viewAllDamageReports() {
        ArrayList<DamageReport> reports = inventorySystem.getDamageReports();
        showHeader("ALL DAMAGE REPORTS");
        if (reports.isEmpty()) showMessage("No damage reports found.");
        else for (DamageReport report : reports) printReportDetails(report);
        pause();
    }

    private void searchDamageReport() {
        showHeader("SEARCH DAMAGE REPORT");
        System.out.print("Enter Report ID: ");
        DamageReport report = findDamageReport(readLine());
        if (report == null) showMessage("Damage report not found.");
        else printReportDetails(report);
        pause();
    }

    private void filterDamageReportsBySeverity() {
        showHeader("FILTER DAMAGE REPORTS");
        DamageSeverity[] severities = DamageSeverity.values();
        for (int i = 0; i < severities.length; i++) System.out.printf("[%d] %s%n", i + 1, severities[i]);
        System.out.println("[0] Back");
        System.out.print("Enter your choice: ");
        int choice = readInt(0, severities.length);
        if (choice == 0) return;

        DamageSeverity selected = severities[choice - 1];
        ArrayList<DamageReport> matches = new ArrayList<>();
        for (DamageReport report : inventorySystem.getDamageReports()) {
            if (report.getSeverity() == selected) matches.add(report);
        }

        showHeader("DAMAGE REPORTS: " + selected);
        if (matches.isEmpty()) showMessage("No reports found with this severity.");
        else for (DamageReport report : matches) printReportDetails(report);
        pause();
    }

    private void manageDamageReportMenu() {
        ArrayList<DamageReport> unresolved = new ArrayList<>();
        for (DamageReport report : inventorySystem.getDamageReports()) {
            if (report.getStatus() != DamageReportStatus.RESOLVED) unresolved.add(report);
        }

        showHeader("UNRESOLVED DAMAGE REPORTS");
        if (unresolved.isEmpty()) {
            showMessage("No unresolved damage reports.");
            pause();
            return;
        }

        for (DamageReport report : unresolved) printReportDetails(report);
        System.out.println("[1] Resolve Damage Report");
        System.out.println("[0] Back");
        System.out.print("Enter your choice: ");
        if (readInt(0, 1) == 1) resolveDamageReport();
    }

    private void resolveDamageReport() {
        showHeader("RESOLVE DAMAGE REPORT");
        System.out.print("Enter Report ID: ");
        DamageReport report = findDamageReport(readLine());
        if (report == null) {
            showMessage("Damage report not found.");
            pause();
            return;
        }
        if (report.getStatus() == DamageReportStatus.RESOLVED) {
            showMessage("This report is already resolved.");
            pause();
            return;
        }

        printReportDetails(report);
        System.out.print("Enter Action Taken: ");
        String actionTaken = readLine();
        if (!confirmAction("Resolve this damage report")) showMessage("Resolution cancelled.");
        else if (custodian.resolveDamageReport(report.getReportID(), actionTaken, inventorySystem)) showMessage("Damage report resolved successfully.");
        else showMessage("Failed to resolve damage report.");
        pause();
    }

    private DamageReport findDamageReport(String id) {
        for (DamageReport report : inventorySystem.getDamageReports()) {
            if (report.getReportID().equalsIgnoreCase(id)) return report;
        }
        return null;
    }

    // REPORTS AND SUMMARY
    private void reportsAndSummaryMenu() {
        boolean back = false;
        while (!back) {
            showHeader("REPORTS AND SUMMARY");
            System.out.println("[1] Inventory Summary");
            System.out.println("[2] Equipment Availability Summary");
            System.out.println("[3] Borrowing Request Summary");
            System.out.println("[0] Return to Dashboard");
            System.out.print("Enter your choice: ");
            switch (readInt(0, 3)) {
                case 1: showMessage(inventorySystem.generateSummaryReport()); pause(); break;
                case 2: printEquipmentAvailabilitySummary(); break;
                case 3: printBorrowingRequestSummary(); break;
                case 0: back = true; break;
            }
        }
    }

    private void printEquipmentAvailabilitySummary() {
        ArrayList<Equipment> equipment = inventorySystem.getEquipmentList();
        int available = 0, borrowed = 0, unavailable = 0;
        for (Equipment item : equipment) {
            if (item.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE) available++;
            else if (item.getAvailabilityStatus() == AvailabilityStatus.BORROWED) borrowed++;
            else unavailable++;
        }

        showHeader("EQUIPMENT AVAILABILITY SUMMARY");
        showField("Total Equipment", equipment.size());
        showField("Available", available);
        showField("Borrowed", borrowed);
        showField("Unavailable", unavailable);
        pause();
    }

    private void printBorrowingRequestSummary() {
        ArrayList<BorrowRequest> requests = getAllRequests();
        int pending = 0, approved = 0, rejected = 0, released = 0, returned = 0, cancelled = 0;

        for (BorrowRequest request : requests) {
            switch (request.getStatus()) {
                case PENDING: pending++; break;
                case APPROVED: approved++; break;
                case REJECTED: rejected++; break;
                case RELEASED: released++; break;
                case RETURNED: returned++; break;
                case CANCELLED: cancelled++; break;
            }
        }

        showHeader("BORROWING REQUEST SUMMARY");
        showField("Total Requests", requests.size());
        showField("Pending", pending);
        showField("Approved", approved);
        showField("Rejected", rejected);
        showField("Released", released);
        showField("Returned", returned);
        showField("Cancelled", cancelled);
        pause();
    }

    private ArrayList<BorrowRequest> getAllRequests() {
        ArrayList<BorrowRequest> all = new ArrayList<>();
        for (BorrowRequestStatus status : BorrowRequestStatus.values()) {
            all.addAll(custodian.viewBorrowRequests(status, inventorySystem));
        }
        return all;
    }

    // SHARED SELECTION METHODS
    private EquipmentCategory selectCategory(String prompt) {
        showMessage(prompt);
        EquipmentCategory[] categories = EquipmentCategory.values();
        for (int i = 0; i < categories.length; i++) System.out.printf("[%d] %s%n", i + 1, categories[i]);
        System.out.print("Select choice: ");
        return categories[readInt(1, categories.length) - 1];
    }

    private EquipmentCondition selectCondition(String prompt) {
        showMessage(prompt);
        EquipmentCondition[] conditions = EquipmentCondition.values();
        for (int i = 0; i < conditions.length; i++) System.out.printf("[%d] %s%n", i + 1, conditions[i]);
        System.out.print("Select choice: ");
        return conditions[readInt(1, conditions.length) - 1];
    }

    private DamageSeverity selectSeverity(String prompt) {
        showMessage(prompt);
        DamageSeverity[] severities = DamageSeverity.values();
        for (int i = 0; i < severities.length; i++) System.out.printf("[%d] %s%n", i + 1, severities[i]);
        System.out.print("Select choice: ");
        return severities[readInt(1, severities.length) - 1];
    }

    // RECORD DISPLAY METHODS
    private void printEquipmentDetails(Equipment equipment) {
        showHeader(equipment.getName().toUpperCase());
        showField("Equipment ID", equipment.getEquipmentID());
        showField("Category", equipment.getCategory());
        showField("Serial Number", equipment.getSerialNumber());
        showField("Condition", equipment.getCondition());
        showField("Location", equipment.getLocation());
        showField("Availability", equipment.getAvailabilityStatus());
        showSeparator();
    }

    private void printRequestDetails(BorrowRequest request) {
        showHeader("BORROWING REQUEST");
        showField("Request ID", request.getRequestID());
        showField("Borrower", request.getBorrower().getName());
        showField("Equipment", getEquipmentNames(request));
        showField("Purpose", request.getPurpose());
        showField("Date Requested", request.getDateRequested());
        showField("Expected Return", request.getRequestReturnDate());
        showField("Status", request.getStatus());
        if (request.getStatus() == BorrowRequestStatus.REJECTED) showField("Rejection Reason", request.getRejectionReason());
        if (request.getDateReturned() != null) showField("Date Returned", request.getDateReturned());
        showSeparator();
    }

    private void printReturnDetails(ReturnRecord record) {
        BorrowRequest request = record.getBorrowRequest();
        showHeader("RETURN RECORD");
        showField("Return ID", record.getReturnID());
        showField("Request ID", request.getRequestID());
        showField("Borrower", request.getBorrower().getName());
        showField("Equipment", getEquipmentNames(request));
        showField("Date Returned", record.getReturnDate());
        showField("Condition", record.getConditionAtReturn());
        showField("Received By", record.getReceivedBy().getName());
        showField("Late Return", record.isLate() ? "Yes" : "No");
        showSeparator();
    }

    private void printReportDetails(DamageReport report) {
        showHeader("DAMAGE REPORT");
        showField("Report ID", report.getReportID());
        showField("Equipment", getReportEquipment(report));
        showField("Borrower", report.getBorrower().getName());
        showField("Severity", report.getSeverity());
        showField("Description", report.getDescription());
        showField("Status", report.getStatus());
        if (report.getStatus() == DamageReportStatus.RESOLVED) showField("Action Taken", report.getActionTaken());
        showSeparator();
    }

    private String getEquipmentNames(BorrowRequest request) {
        StringBuilder names = new StringBuilder();
        for (Equipment equipment : request.getEquipmentList()) {
            if (names.length() > 0) names.append(", ");
            names.append(equipment.getName());
        }
        return names.length() == 0 ? "No equipment listed" : names.toString();
    }

    private String getReportEquipment(DamageReport report) {
        if (report.getReturnRecord() == null || report.getReturnRecord().getBorrowRequest() == null) return "Unknown equipment";
        return getEquipmentNames(report.getReturnRecord().getBorrowRequest());
    }
}
