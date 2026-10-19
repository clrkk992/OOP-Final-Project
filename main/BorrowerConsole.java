
package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;
import entities.*;
import enums.*;
import service.EquipmentInventorySystem;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - NEW FILE: completes your draft BorrowerConsole and extends the new Console
 *  - CHANGED from your draft: constructor is (Scanner, EquipmentInventorySystem, Borrower) and calls super(input)
 *  - CHANGED from your draft: Logout is [0] (Console.run() exits on 0); menu follows the newest Figma frame, which includes Return Equipment
 *  - executeChoice(): cases 1-7 hold the action logic; case 0 logs out
 *  - Helpers at the end of the class: addEquipmentToRequest, finishRequest, printEquipmentTable, equipmentDetailLoop, requestDetailLoop, printEquipmentDetails, printRequestDetails, printCurrentRequest, readReturnDate
 *  - NEW: Used the formatting methods from Console.java to display headers, fields, messages, and separators
 */

public class BorrowerConsole extends Console {
    private final Borrower borrower;
    private final EquipmentInventorySystem inventorySystem;

    public BorrowerConsole(Scanner input, EquipmentInventorySystem inventorySystem, Borrower borrower) {
        super(input);
        this.inventorySystem = inventorySystem;
        this.borrower = borrower;
    }

    // Displays the borrower's main dashboard.
    @Override
    protected void showConsole() {
        showHeader("BORROWER DASHBOARD");
        showMessage("Welcome, " + borrower.getName());
        showMessage("Borrower ID: " + borrower.getUserID());
        
        System.out.println();
        showMessage("[1] View Available Equipment");
        showMessage("[2] Search Equipment");
        showMessage("[3] Submit Borrow Request");
        showMessage("[4] Cancel Borrow Request");
        showMessage("[5] View My Borrow Requests");
        showMessage("[6] Return Equipment");
        showMessage("[7] View Borrowing History");
        showMessage("[0] Logout");
        System.out.print("Enter your choice: ");
    }

    // Handles the selected dashboard option.
    @Override
    protected void executeChoice(int choice) {
        switch (choice) {
            case 1: viewAvailableEquipment(); break;
            case 2: searchEquipment(); break;
            case 3: createBorrowRequest(); break;
            case 4: cancelBorrowRequest(); break;
            case 5: viewMyBorrowRequests(); break;
            case 6: returnEquipment(); break;
            case 7: viewBorrowingHistory(); break;
            case 0: showMessage("Logged out. Goodbye, " + borrower.getName() + "!"); break;
            default: showMessage("Invalid choice. Please select from the menu.");
        }
    }

    // Displays equipment available for borrowing.
    private void viewAvailableEquipment() {
        showHeader("AVAILABLE EQUIPMENT");
        ArrayList<Equipment> equipment = borrower.viewAvailableEquipment(inventorySystem);
        if (equipment.isEmpty()) {
            showMessage("No equipment is available right now.");
        } else {
            printEquipmentList(equipment);
            equipmentDetailLoop();
        }
        pause();
    }

    // Searches for equipment using an ID or keyword.
    private void searchEquipment() {
        showHeader("SEARCH EQUIPMENT");
        System.out.print("Enter an equipment ID or keyword: ");
        ArrayList<Equipment> results = borrower.searchEquipment(readLine(), inventorySystem);
        if (results.isEmpty()) {
            showMessage("No equipment found.");
        } else {
            printEquipmentList(results);
            equipmentDetailLoop();
        }
        pause();
    }

    // Creates a request containing one or more equipment items.
    private void createBorrowRequest() {
        ArrayList<Equipment> requestItems = new ArrayList<>();
        while (true) {
            showHeader("CREATE BORROW REQUEST");
            printCurrentRequest(requestItems);
            showMessage("[1] Add Equipment");
            showMessage("[2] Submit Request");
            showMessage("[0] Back");
            System.out.print("Enter your choice: ");
            int choice = readInt(0, 2);
            if (choice == 0) {
                showMessage("Borrow request cancelled.");
                return;
            } else if (choice == 1) {
                addEquipmentToRequest(requestItems);
            } else if (requestItems.isEmpty()) {
                showMessage("Add at least one equipment first.");
            } else if (finishRequest(requestItems)) {
                return;
            }
        }
    }

    // Adds an available item to the request without duplicates.
    private void addEquipmentToRequest(ArrayList<Equipment> requestItems) {
        showHeader("ADD EQUIPMENT");
        System.out.print("Enter Equipment ID: ");
        Equipment equipment = borrower.viewEquipmentDetails(readLine(), inventorySystem);
        if (equipment == null) {
            showMessage("Equipment not found.");
            return;
        }
        if (!equipment.checkAvailability()) {
            showMessage("This equipment is not available.");
            return;
        }
        for (Equipment item : requestItems) {
            if (item.getEquipmentID().equals(equipment.getEquipmentID())) {
                showMessage("This equipment is already in your request.");
                return;
            }
        }
        requestItems.add(equipment);
        showMessage("Equipment added to your request.");
    }

    // Collects the request details and submits the request.
    private boolean finishRequest(ArrayList<Equipment> requestItems) {
        showHeader("SUBMIT BORROW REQUEST");
        System.out.print("Enter purpose of borrowing: ");
        String purpose = readLine();
        String returnDate = readReturnDate();
        if (!confirmAction("Submit this borrow request")) {
            showMessage("Request submission cancelled.");
            return false;
        }
        BorrowRequest request = borrower.submitBorrowRequest(new ArrayList<>(requestItems), purpose, returnDate, inventorySystem);
        if (request == null) {
            showMessage("Unable to submit the borrow request.");
            return false;
        }
        showHeader("REQUEST SUBMITTED");
        showField("Request ID", request.getRequestID());
        showField("Date Requested", request.getDateRequested());
        showField("Status", request.getStatus().toString());
        pause();
        return true;
    }

    // Displays pending requests and allows the borrower to cancel one.
    private void cancelBorrowRequest() {
        showHeader("CANCEL BORROW REQUEST");
        ArrayList<BorrowRequest> pending = borrower.viewRequestsByStatus(BorrowRequestStatus.PENDING);
        if (pending.isEmpty()) {
            showMessage("You have no pending requests.");
            pause();
            return;
        }
        printRequestList(pending);
        showMessage("[1] Cancel Request");
        showMessage("[0] Back");
        System.out.print("Enter your choice: ");
        if (readInt(0, 1) == 0) return;
        System.out.print("Enter Borrow Request ID: ");
        BorrowRequest request = borrower.findBorrowRequest(readLine());
        if (request == null || request.getStatus() != BorrowRequestStatus.PENDING) {
            showMessage("Pending request not found.");
            pause();
            return;
        }
        printRequestDetails(request);
        if (confirmAction("Confirm cancellation")) {
            if (borrower.cancelBorrowRequest(request.getRequestID())) {
                showMessage("Borrow request cancelled successfully.");
            } else {
                showMessage("Request was not cancelled.");
            }
        }
        pause();
    }

    // Displays all requests submitted by the borrower.
    private void viewMyBorrowRequests() {
        showHeader("MY BORROW REQUESTS");
        ArrayList<BorrowRequest> requests = borrower.viewBorrowingHistory();
        if (requests.isEmpty()) {
            showMessage("You have no borrow requests yet.");
        } else {
            printRequestList(requests);
            requestDetailLoop();
        }
        pause();
    }

    // Allows the borrower to file a return notice for released equipment.
    private void returnEquipment() {
        showHeader("RETURN EQUIPMENT");
        ArrayList<BorrowRequest> returnable = borrower.viewReturnableRequests();
        if (returnable.isEmpty()) {
            showMessage("You have no equipment awaiting return.");
            pause();
            return;
        }
        printRequestList(returnable);
        showMessage("[1] File Return Notice");
        showMessage("[0] Back");
        System.out.print("Enter your choice: ");
        if (readInt(0, 1) == 0) return;
        System.out.print("Enter Borrow Request ID: ");
        String requestID = readLine();
        BorrowRequest request = findRequestByID(returnable, requestID);
        if (request == null || request.isReturnRequested()) {
            showMessage("Returnable request not found or return notice already filed.");
            pause();
            return;
        }
        printRequestDetails(request);
        EquipmentCondition[] conditions = EquipmentCondition.values();
        showHeader("SELECT EQUIPMENT CONDITION");
        for (int i = 0; i < conditions.length; i++) {
            showMessage("[" + (i + 1) + "] " + conditions[i]);
        }
        System.out.print("Enter your choice: ");
        EquipmentCondition condition = conditions[readInt(1, conditions.length) - 1];
        if (confirmAction("Confirm return notice")) {
            if (borrower.returnEquipment(requestID, condition)) {
                showMessage("Return notice filed successfully. Please hand the equipment to the custodian.");
                showField("Request ID", requestID);
                showField("Declared Condition", condition.toString());
            } else {
                showMessage("Return notice was not filed.");
            }
        }
        pause();
    }

    // Displays completed borrowing records.
    private void viewBorrowingHistory() {
        showHeader("MY BORROWING HISTORY");
        ArrayList<BorrowRequest> history = borrower.viewReturnedHistory();
        if (history.isEmpty()) {
            showMessage("No completed borrows yet.");
        } else {
            printRequestList(history);
            showField("Total Records", String.valueOf(history.size()));
            requestDetailLoop();
        }
        pause();
    }

    // Displays equipment as separate record blocks.
    private void printEquipmentList(ArrayList<Equipment> equipmentList) {
        for (Equipment equipment : equipmentList) {
            showSeparator();
            showField("Equipment ID", equipment.getEquipmentID());
            showField("Name", equipment.getName());
            showField("Category", equipment.getCategory().toString());
            showField("Condition", equipment.getCondition().toString());
            showField("Availability", equipment.getAvailabilityStatus().toString());
        }
        showSeparator();
        showField("Total Equipment", String.valueOf(equipmentList.size()));
    }

    // Displays requests as separate record blocks.
    private void printRequestList(ArrayList<BorrowRequest> requests) {
        for (BorrowRequest request : requests) {
            showSeparator();
            showField("Request ID", request.getRequestID());
            showField("Date Requested", request.getDateRequested());
            showField("Status", request.getStatus().toString());
        }
        showSeparator();
        showField("Total Requests", String.valueOf(requests.size()));
    }

    // Allows the borrower to inspect equipment details.
    private void equipmentDetailLoop() {
        while (true) {
            showMessage("[1] View Equipment Details");
            showMessage("[0] Back");
            System.out.print("Enter your choice: ");
            if (readInt(0, 1) == 0) return;
            System.out.print("Enter Equipment ID: ");
            Equipment equipment = borrower.viewEquipmentDetails(readLine(), inventorySystem);
            if (equipment == null) {
                showMessage("Equipment not found.");
            } else {
                printEquipmentDetails(equipment);
            }
            pause();
        }
    }

    // Displays all details of one equipment item.
    private void printEquipmentDetails(Equipment equipment) {
        showHeader("EQUIPMENT DETAILS");
        showField("Equipment ID", equipment.getEquipmentID());
        showField("Name", equipment.getName());
        showField("Category", equipment.getCategory().toString());
        showField("Serial Number", equipment.getSerialNumber());
        showField("Condition", equipment.getCondition().toString());
        showField("Location", equipment.getLocation());
        showField("Availability", equipment.getAvailabilityStatus().toString());
        showSeparator();
    }

    // Allows the borrower to inspect an individual request.
    private void requestDetailLoop() {
        while (true) {
            showMessage("[1] View Request Details");
            showMessage("[0] Back");
            System.out.print("Enter your choice: ");
            if (readInt(0, 1) == 0) return;
            System.out.print("Enter Borrow Request ID: ");
            BorrowRequest request = borrower.findBorrowRequest(readLine());
            if (request == null) {
                showMessage("Borrow request not found.");
            } else {
                printRequestDetails(request);
            }
            pause();
        }
    }

    // Displays the complete details of a borrowing request.
    private void printRequestDetails(BorrowRequest request) {
        showHeader("BORROW REQUEST DETAILS");
        showField("Request ID", request.getRequestID());
        showMessage("Equipment:");
        for (Equipment equipment : request.getEquipmentList()) {
            showField(equipment.getEquipmentID(), equipment.getName());
        }
        showField("Purpose", request.getPurpose());
        showField("Date Requested", request.getDateRequested());
        showField("Expected Return", request.getRequestReturnDate());
        showField("Status", request.getStatus().toString());
        if (request.getRejectionReason() != null && !request.getRejectionReason().isBlank()) {
            showField("Rejection Reason", request.getRejectionReason());
        }
        if (request.getDateBorrowed() != null && !request.getDateBorrowed().isBlank()) {
            showField("Date Borrowed", request.getDateBorrowed());
        }
        if (request.getDateReturned() != null && !request.getDateReturned().isBlank()) {
            showField("Date Returned", request.getDateReturned());
        }
        showSeparator();
    }

    // Displays the equipment currently being added to a new request.
    private void printCurrentRequest(ArrayList<Equipment> requestItems) {
        showMessage("Equipment in current request:");
        if (requestItems.isEmpty()) {
            showMessage("No equipment added yet.");
            return;
        }
        for (Equipment equipment : requestItems) {
            showField(equipment.getEquipmentID(), equipment.getName());
        }
        showField("Item Count", String.valueOf(requestItems.size()));
        showSeparator();
    }

    // Finds a request among those eligible for return.
    private BorrowRequest findRequestByID(ArrayList<BorrowRequest> requests, String requestID) {
        for (BorrowRequest request : requests) {
            if (request.getRequestID().equalsIgnoreCase(requestID)) return request;
        }
        return null;
    }

    // Validates the expected return date and disallows past dates.
    private String readReturnDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            System.out.print("Enter expected return date (MM/DD/YYYY): ");
            String date = readLine();
            try {
                LocalDate returnDate = LocalDate.parse(date, formatter);
                if (returnDate.isBefore(LocalDate.now())) {
                    showMessage("Return date cannot be in the past.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                showMessage("Invalid date. Use MM/DD/YYYY.");
            }
        }
    }
}
