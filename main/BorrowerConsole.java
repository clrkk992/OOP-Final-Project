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
 */
public class BorrowerConsole extends Console {

    private final Borrower borrower;
    private final EquipmentInventorySystem inventorySystem;

    public BorrowerConsole(Scanner input, EquipmentInventorySystem inventorySystem, Borrower borrower) {
        super(input);
        this.inventorySystem = inventorySystem;
        this.borrower = borrower;
    }

    // Shows the borrower dashboard.
    @Override
    protected void showConsole() {
        System.out.println("\n=== BORROWER DASHBOARD ====");
        System.out.printf("Welcome, %s (ID: %s)%n", borrower.getName(), borrower.getUserID());
        System.out.println("[1] View Available Equipment");
        System.out.println("[2] Search Equipment");
        System.out.println("[3] Submit Borrow Request");
        System.out.println("[4] Cancel Borrow Request");
        System.out.println("[5] View My Borrow Requests");
        System.out.println("[6] Return Equipment");
        System.out.println("[7] View Borrowing History");
        System.out.println("[0] Logout");
        System.out.print("Enter your choice: ");
    }

    // Runs the selected borrower action.
    @Override
    protected void executeChoice(int choice) {
        switch (choice) {

            // ===== [1] VIEW AVAILABLE EQUIPMENT =====
            case 1: {
                ArrayList<Equipment> available = borrower.viewAvailableEquipment(inventorySystem);

                System.out.println("\n========== ALL AVAILABLE EQUIPMENT =============");
                if (available.isEmpty()) {
                    System.out.println("No equipment is available right now.");
                    pause();
                    return;
                }

                printEquipmentTable(available);
                equipmentDetailLoop();
                break;
            }

            // ===== [2] SEARCH EQUIPMENT =====
            case 2: {
                System.out.println("\n========= SEARCH EQUIPMENT ===========");
                System.out.print("Enter Equipment ID or name: ");
                String keyword = readLine();

                ArrayList<Equipment> results = borrower.searchEquipment(keyword, inventorySystem);
                if (results.isEmpty()) {
                    System.out.println("No equipment found.");
                    pause();
                    return;
                }

                printEquipmentTable(results);
                equipmentDetailLoop();
                break;
            }

            // ===== [3] SUBMIT BORROW REQUEST =====
            case 3: {
                ArrayList<Equipment> requestItems = new ArrayList<>();

                while (true) {
                    System.out.println("\n===== CREATE A BORROW REQUEST =====");
                    printCurrentRequest(requestItems);
                    System.out.println("[1] Add Equipment");
                    System.out.println("[2] Submit Request");
                    System.out.println("[0] Back");
                    System.out.print("Enter choice: ");

                    int option = readInt(0, 2);
                    if (option == 0) {
                        System.out.println("Borrow request cancelled.");
                        return;
                    }

                    if (option == 1) {
                        addEquipmentToRequest(requestItems);
                    } else if (requestItems.isEmpty()) {
                        System.out.println("Add at least one equipment first.");
                    } else if (finishRequest(requestItems)) {
                        return;
                    }
                }
            }

            // ===== [4] CANCEL BORROW REQUEST =====
            case 4: {
                ArrayList<BorrowRequest> pending = borrower.viewRequestsByStatus(BorrowRequestStatus.PENDING);

                System.out.println("\n=========== PENDING REQUESTS =============");
                if (pending.isEmpty()) {
                    System.out.println("You have no pending requests.");
                    pause();
                    return;
                }

                System.out.printf("%-6s | %-12s | %s%n", "ID", "Date", "Status");
                System.out.println("---------------------------------------");
                for (BorrowRequest item : pending) {
                    System.out.printf("%-6s | %-12s | %s%n", item.getRequestID(), item.getDateRequested(), item.getStatus());
                }

                System.out.println("[1] Cancel Request");
                System.out.println("[0] Back");
                System.out.print("Enter your choice: ");
                if (readInt(0, 1) == 0) {
                    return;
                }

                System.out.print("Enter Borrow Request ID: ");
                BorrowRequest request = borrower.findBorrowRequest(readLine());

                if (request == null || request.getStatus() != BorrowRequestStatus.PENDING) {
                    System.out.println("Pending request not found.");
                    pause();
                    return;
                }

                printRequestDetails(request);
                System.out.print("Confirm Cancellation [Y/N]: ");
                if (confirmation() && borrower.cancelBorrowRequest(request.getRequestID())) {
                    System.out.println("Borrow request cancelled.");
                } else {
                    System.out.println("Request was not cancelled.");
                }
                pause();
                break;
            }

            // ===== [5] VIEW MY BORROW REQUESTS =====
            case 5: {
                ArrayList<BorrowRequest> requests = borrower.viewBorrowingHistory();

                System.out.println("\n=========== MY REQUESTS ==============");
                if (requests.isEmpty()) {
                    System.out.println("You have no borrow requests yet.");
                    pause();
                    return;
                }

                System.out.printf("%-6s | %-12s | %s%n", "ID", "Date", "Status");
                System.out.println("---------------------------------------");
                for (BorrowRequest item : requests) {
                    System.out.printf("%-6s | %-12s | %s%n", item.getRequestID(), item.getDateRequested(), item.getStatus());
                }

                requestDetailLoop();
                break;
            }

            // ===== [6] RETURN EQUIPMENT =====
            case 6: {
                ArrayList<BorrowRequest> borrowed = borrower.viewReturnableRequests();

                System.out.println("\n========== RETURN EQUIPMENT ============");
                if (borrowed.isEmpty()) {
                    System.out.println("You have no borrowed equipment to return.");
                    pause();
                    return;
                }

                System.out.println("Currently Borrowed Requests:");
                System.out.printf("%-6s | %-13s | %-15s | %s%n", "ID", "Date Borrowed", "Expected Return", "Return Notice");
                System.out.println("-----------------------------------------------------------");
                for (BorrowRequest item : borrowed) {
                    System.out.printf("%-6s | %-13s | %-15s | %s%n", item.getRequestID(), item.getDateBorrowed(),
                            item.getRequestReturnDate(), item.isReturnRequested() ? "Filed" : "Not yet");
                }

                System.out.println("[1] Return Equipment");
                System.out.println("[0] Back");
                System.out.print("Enter your choice: ");
                if (readInt(0, 1) == 0) {
                    return;
                }

                System.out.print("Enter Borrow Request ID: ");
                BorrowRequest request = borrower.findBorrowRequest(readLine());

                if (request == null || request.getStatus() != BorrowRequestStatus.RELEASED) {
                    System.out.println("Borrowed request not found.");
                    pause();
                    return;
                }

                printRequestDetails(request);

                EquipmentCondition[] conditions = EquipmentCondition.values();
                System.out.println("Select condition:");
                for (int i = 0; i < conditions.length; i++) {
                    System.out.printf("[%d] %s%n", i + 1, conditions[i]);
                }
                System.out.print("Enter choice: ");
                EquipmentCondition condition = conditions[readInt(1, conditions.length) - 1];

                System.out.print("Confirm Return [Y/N]: ");
                if (confirmation() && borrower.returnEquipment(request.getRequestID(), condition)) {
                    System.out.println("Return notice filed. Please hand the equipment to the custodian.");
                    System.out.printf("%-18s: %s%n", "Request ID", request.getRequestID());
                    System.out.printf("%-18s: %s%n", "Declared Condition", condition);
                } else {
                    System.out.println("Return notice was not filed.");
                }
                pause();
                break;
            }

            // ===== [7] VIEW BORROWING HISTORY =====
            case 7: {
                ArrayList<BorrowRequest> history = borrower.viewReturnedHistory();

                System.out.println("\n=========== MY BORROWING HISTORY ==============");
                if (history.isEmpty()) {
                    System.out.println("No completed borrows yet.");
                    pause();
                    return;
                }

                System.out.printf("%-6s | %-13s | %s%n", "ID", "Date Borrowed", "Date Returned");
                System.out.println("---------------------------------------");
                for (BorrowRequest item : history) {
                    System.out.printf("%-6s | %-13s | %s%n", item.getRequestID(), item.getDateBorrowed(), item.getDateReturned());
                }
                System.out.println("---------------------------------------");
                System.out.printf("Total records: %d%n", history.size());

                requestDetailLoop();
                break;
            }

            case 0:
                System.out.printf("Logged out. Goodbye, %s!%n", borrower.getName());
                break;

            default:
                System.out.println("Invalid choice, please pick from the menu.");
        }
    }

    // ===== HELPERS =====

    // Adds one equipment (by ID) to the request being built.
    private void addEquipmentToRequest(ArrayList<Equipment> requestItems) {
        System.out.print("Enter Equipment ID: ");
        Equipment equipment = borrower.viewEquipmentDetails(readLine(), inventorySystem);

        if (equipment == null) {
            System.out.println("Equipment not found.");
        } else if (!equipment.checkAvailability()) {
            System.out.println("Equipment is not available.");
        } else if (requestItems.contains(equipment)) {
            System.out.println("Equipment is already in this request.");
        } else {
            requestItems.add(equipment);
            System.out.println("Equipment added successfully.");
        }
    }

    // Asks for the return date and purpose, then submits. Returns true when the request was submitted.
    private boolean finishRequest(ArrayList<Equipment> requestItems) {
        System.out.print("Requested Return Date [mm/dd/yyyy]: ");
        String returnDate = readReturnDate();
        System.out.print("Purpose: ");
        String purpose = readLine();
        System.out.print("Submit Request? [Y/N]: ");

        if (!confirmation()) {
            System.out.println("Submission cancelled. You can keep editing the request.");
            return false;
        }

        BorrowRequest request = borrower.submitBorrowRequest(new ArrayList<>(requestItems), purpose, returnDate, inventorySystem);
        if (request == null) {
            System.out.println("Unable to submit the request. Please check your details.");
            return false;
        }

        System.out.println("Borrow request submitted.");
        System.out.printf("%-16s: %s%n", "Request ID", request.getRequestID());
        System.out.printf("%-16s: %s%n", "Date Requested", request.getDateRequested());
        System.out.printf("%-16s: %s%n", "Expected Return", request.getRequestReturnDate());
        System.out.printf("%-16s: %s%n", "Status", request.getStatus());
        pause();
        return true;
    }

    // Table of equipment (ID | Name | Category).
    private void printEquipmentTable(ArrayList<Equipment> list) {
        System.out.printf("%-6s | %-20s | %s%n", "ID", "Name", "Category");
        System.out.println("-----------------------------------------------");
        for (Equipment equipment : list) {
            System.out.printf("%-6s | %-20s | %s%n", equipment.getEquipmentID(), equipment.getName(), equipment.getCategory());
        }
        System.out.println("-----------------------------------------------");
    }

    // Lets the borrower open the details of one equipment by ID, until they exit.
    private void equipmentDetailLoop() {
        while (true) {
            System.out.println("[1] Get Detailed View of Equipment");
            System.out.println("[0] Exit");
            System.out.print("Enter your choice: ");
            if (readInt(0, 1) == 0) {
                return;
            }

            System.out.print("Enter Equipment ID: ");
            Equipment equipment = borrower.viewEquipmentDetails(readLine(), inventorySystem);
            if (equipment == null) {
                System.out.println("Equipment not found.");
            } else {
                printEquipmentDetails(equipment);
            }
            pause();
        }
    }

    // Lets the borrower open the details of one of their requests by ID, until they exit.
    private void requestDetailLoop() {
        while (true) {
            System.out.println("[1] Get Detailed View of Request");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            if (readInt(0, 1) == 0) {
                return;
            }

            System.out.print("Enter Borrow Request ID: ");
            BorrowRequest request = borrower.findBorrowRequest(readLine());
            if (request == null) {
                System.out.println("Request not found.");
            } else {
                printRequestDetails(request);
            }
            pause();
        }
    }

    private void printEquipmentDetails(Equipment equipment) {
        System.out.println("--------------------------------------");
        System.out.println(equipment.getName().toUpperCase());
        System.out.println("--------------------------------------");
        System.out.printf("%-13s: %s%n", "EquipmentID", equipment.getEquipmentID());
        System.out.printf("%-13s: %s%n", "Category", equipment.getCategory());
        System.out.printf("%-13s: %s%n", "SerialNo", equipment.getSerialNumber());
        System.out.printf("%-13s: %s%n", "Condition", equipment.getCondition());
        System.out.printf("%-13s: %s%n", "Location", equipment.getLocation());
        System.out.printf("%-13s: %s%n", "Availability", equipment.getAvailabilityStatus());
        System.out.println("======================================");
    }

    private void printRequestDetails(BorrowRequest request) {
        System.out.printf("%-16s: %s%n", "Request ID", request.getRequestID());
        System.out.println("Equipment/s:");
        System.out.println("--------------------------------------");
        for (Equipment equipment : request.getEquipmentList()) {
            System.out.printf("[%s] %s%n", equipment.getEquipmentID(), equipment.getName());
        }
        System.out.println("--------------------------------------");
        System.out.printf("%-16s: %d%n", "Total equipment", request.getEquipmentList().size());
        System.out.printf("%-16s: %s%n", "Purpose", request.getPurpose());
        System.out.printf("%-16s: %s%n", "Date Requested", request.getDateRequested());
        System.out.printf("%-16s: %s%n", "Expected Return", request.getRequestReturnDate());
        System.out.printf("%-16s: %s%n", "Status", request.getStatus());

        if (request.getStatus() == BorrowRequestStatus.REJECTED) {
            System.out.printf("%-16s: %s%n", "Rejection Reason", request.getRejectionReason());
        }
        if (request.getDateBorrowed() != null) {
            System.out.printf("%-16s: %s%n", "Date Borrowed", request.getDateBorrowed());
        }
        if (request.getDateReturned() != null) {
            System.out.printf("%-16s: %s%n", "Date Returned", request.getDateReturned());
        }
        System.out.println("======================================");
    }

    private void printCurrentRequest(ArrayList<Equipment> requestItems) {
        System.out.println("Current Request:");
        System.out.println("---------------------------");
        if (requestItems.isEmpty()) {
            System.out.println("(empty)");
        }
        for (Equipment equipment : requestItems) {
            System.out.printf("[%s] %s%n", equipment.getEquipmentID(), equipment.getName());
        }
        System.out.println("---------------------------");
    }

    // Reads a mm/dd/yyyy date that is today or later.
    private String readReturnDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

        while (true) {
            String text = readLine();
            try {
                LocalDate date = LocalDate.parse(text, format);
                if (!date.isBefore(LocalDate.now())) {
                    return text;
                }
                System.out.print("Return date cannot be in the past, try again: ");
            } catch (DateTimeParseException e) {
                System.out.print("Use mm/dd/yyyy, try again: ");
            }
        }
    }
}
