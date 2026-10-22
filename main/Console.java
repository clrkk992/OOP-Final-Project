package main;

import entities.Borrower;
import entities.Custodian;
import entities.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import service.EquipmentInventorySystem;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - CHANGED: input is now final and passed in through a constructor (it was never set, so the first read crashed)
 *  - CHANGED: readInt(), readDouble(), confirmation() read a whole line with nextLine() and parse it (fixes the leftover Enter bug and the try/catch that never caught anything)
 *  - NEW: readInt(min, max)
 *  - NEW: pause() for "Press Enter to continue..."
 *  - UNCHANGED: run(), readLine(), and the abstract methods showConsole() / executeChoice()
 *  - NEW: runSystem() static method that handles the initial login and sign-up menu, and routes to the appropriate console.
 *  - NEW: handleLogin() and handleSignUp() static methods to handle the login and sign-up flows.
 *  - NEW: hanldeSignUp() checks for duplicate emails and rejects them.
 *  - NEW: showHeader(), showSeparator(), showField(), showMessage(), showOptions(), confirmAction() formatting methods for consistent console output across all consoles.
 * Search for "NEW:" and "CHANGED:" to find each change.
 */
public abstract class Console {

    // CHANGED: now final and set through the constructor. One shared Scanner, passed in by the main class,
    // so every console reads from the same System.in.
    protected final Scanner input;

    // NEW: constructor (subclasses call super(input))
    protected Console(Scanner input) {
        this.input = input;
    }

    // Each console supplies its own menu and menu actions.
    protected abstract void showConsole();
    protected abstract void executeChoice(int choice);

    //Main static entry point that handles initial startup, user login, and account sign-up.
    //Routes successful logins to the appropriate role console.
    public static void runSystem(Scanner scanner, EquipmentInventorySystem inventorySystem) {
        boolean running = true;
        System.out.println("==================================================");
        System.out.println("  EQUIPMENT BORROWING AND ACCOUNTABILITY SYSTEM   ");
        System.out.println("==================================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("[1] Login");
            System.out.println("[2] Sign Up (Create Account)");
            System.out.println("[0] Exit System");
            System.out.print("Enter choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            if (choice == 0) {
                running = false;
                System.out.println("Thank you for using the system. Goodbye!");
            } else if (choice == 1) {
                handleLogin(scanner, inventorySystem);
            } else if (choice == 2) {
                handleSignUp(scanner, inventorySystem);
            } else {
                System.out.println("Invalid choice. Please select 0, 1, or 2.");
            }
        }
    }

    
    //Handles credential authentication and launches the corresponding role console.
    private static void handleLogin(Scanner scanner, EquipmentInventorySystem inventorySystem) {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        User user = inventorySystem.authenticate(email, password);

        if (user == null) {
            System.out.println("Authentication failed! Invalid email, password, or account is blocked.");
            return;
        }

        System.out.printf("Login successful! Welcome, %s (%s)%n", user.getName(), user.displayRole());

        // Route user to their respective console dashboard
        if (user instanceof Borrower) {
            BorrowerConsole borrowerConsole = new BorrowerConsole(scanner, inventorySystem, (Borrower) user);
            borrowerConsole.run();
        } else if (user instanceof Custodian) {
            CustodianConsole custodianConsole = new CustodianConsole(scanner, inventorySystem, (Custodian) user);
            custodianConsole.run();
        } else {
            System.out.println("Error: Unknown user role encountered.");
        }
    }

    
    //Handles registration for new Borrower or Custodian accounts.
    private static void handleSignUp(Scanner scanner, EquipmentInventorySystem inventorySystem) {
        System.out.println("\n--- CREATE NEW ACCOUNT ---");
        System.out.println("[1] Register as Borrower");
        System.out.println("[2] Register as Custodian");
        System.out.print("Select role: ");

        int roleChoice = -1;
        try {
            roleChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Registration cancelled.");
            return;
        }

        if (roleChoice != 1 && roleChoice != 2) {
            System.out.println("Invalid role selection.");
            return;
        }

        //Changed: added validation loop each
        String name;
            while (true) {
                System.out.print("Enter Full Name: ");
                name = scanner.nextLine().trim();

                if (name.isBlank()) {
                    System.out.println("Name cannot be blank.");
                    continue;
                }

                break;
            }

        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine().trim();
            
            if (email.isBlank()) {
            System.out.println("Email cannot be blank.");
            continue;
            }
            
            if (!email.contains("@")) {
            System.out.println("Email must contain '@'.");
            continue;
            }
            
            if (!email.endsWith(".com")) {
            System.out.println("Email must end with '.com'.");
            continue;
            }
            
            if (inventorySystem.emailExists(email)) {
            System.out.println("Registration failed: Email is already registered in the system.");
            return;
            }

            break;
        }

        String password;
        while (true) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine().trim();

            if (password.isBlank()) {
                System.out.println("Password cannot be blank.");
                continue;
            }

            if (password.length() < 4) {
                System.out.println("Password must be at least 4 characters.");
                continue;
            }

            break;
        }

        if (roleChoice == 1) {
            Borrower newBorrower = new Borrower(name, email, password);
            if (inventorySystem.registerBorrower(newBorrower)) {
                System.out.printf("Borrower account created successfully! Your ID is %s%n", newBorrower.getUserID());
            } else {
                System.out.println("Failed to register borrower.");
            }
        } else {
            String storageArea;
            while (true) {
                System.out.print("Enter Assigned Storage Area: ");
                storageArea = scanner.nextLine().trim();

                if (storageArea.isBlank()) {
                    System.out.println("Assigned storage area cannot be blank.");
                    continue;
                }

                break;
            }

            Custodian newCustodian = new Custodian(name, email, password, storageArea);
            if (inventorySystem.registerCustodian(newCustodian)) {
                System.out.printf("Custodian account created successfully! Your ID is %s%n", newCustodian.getUserID());
            } else {
                System.out.println("Failed to register custodian.");
            }
        }
    }

    // Repeats the menu until the console's exit choice (0) is selected.
    public void run() {
        boolean running = true;
        while (running) {
            showConsole();
            int choice = readInt();
            executeChoice(choice);

            if (choice == 0) {
                running = false;
            }
        }
    }

    // Reads a non-empty line of text.
    protected String readLine() {
        while (true) {
            String line = input.nextLine();
            if (line.isEmpty()) {
                System.out.print("Input cannot be empty, try again: ");
            } else if (line.isBlank()) {
                System.out.print("Input cannot be blank, try again: ");
            } else {
                return line.trim();
            }
        }
    }

    // CHANGED: reads a whole line and parses it. Reads a non-negative integer and retries invalid input.
    protected int readInt() {
        while (true) {
            String line = input.nextLine().trim();
            try {
                int number = Integer.parseInt(line);
                if (number >= 0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // falls through to the retry message
            }
            System.out.print("Invalid number, try again: ");
        }
    }

    // NEW: Reads an integer from min to max (inclusive) and retries invalid input.
    protected int readInt(int min, int max) {
        while (true) {
            String line = input.nextLine().trim();
            try {
                int number = Integer.parseInt(line);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // falls through to the retry message
            }
            System.out.print("Invalid choice, enter " + min + " to " + max + ": ");
        }
    }

    // CHANGED: reads a whole line and parses it. Reads a finite decimal number and retries invalid input.
    // protected double readDouble() {
    //     while (true) {
    //         String line = input.nextLine().trim();
    //         try {
    //             double number = Double.parseDouble(line);
    //             if (Double.isFinite(number)) {
    //                 return number;
    //             }
    //         } catch (NumberFormatException e) {
    //             // falls through to the retry message
    //         }
    //         System.out.print("Invalid number, try again: ");
    //     }
    // }

    protected String formatDate(String date) {
        if (date == null || date.isEmpty()) return "N/A";

        LocalDate parsedDate = LocalDate.parse(date);
        return parsedDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    // CHANGED: uses nextLine() instead of next(). Reads a Y/N answer and returns true for Y or false for N.
    protected boolean confirmation() {
        while (true) {
            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            } else if (answer.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.print("Please enter Y or N: ");
        }
    }

    // NEW: Waits for Enter, for the "Press Enter to continue..." screens.
    protected void pause() {
        System.out.print("Press Enter to continue...");
        input.nextLine();
    }

    // NEW - Formatting methods for consistent console output across all consoles.
    protected void showHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("          " + title);
        System.out.println("========================================");
    }

    protected void showSeparator() {
        System.out.println("----------------------------------------");
    }

    protected void showField(String label, Object value) {
        System.out.printf("%-25s: %s%n", label, value);
    }

    protected void showMessage(String message) {
        System.out.println(message);
    }

    protected void showOptions(String... options) {
        for (int i = 0; i < options.length; i++) {
            System.out.printf("[%d] %s%n", i + 1, options[i]);
        }
        System.out.println("[0] Back");
    }

    protected boolean confirmAction(String message) {
        System.out.print(message + " [Y/N]: ");
        return confirmation();
    }
}
