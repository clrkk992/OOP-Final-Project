package main;

import java.util.Scanner;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - CHANGED: input is now final and passed in through a constructor (it was never set, so the first read crashed)
 *  - CHANGED: readInt(), readDouble(), confirmation() read a whole line with nextLine() and parse it (fixes the leftover Enter bug and the try/catch that never caught anything)
 *  - NEW: readInt(min, max)
 *  - NEW: pause() for "Press Enter to continue..."
 *  - UNCHANGED: run(), readLine(), and the abstract methods showConsole() / executeChoice()
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
    protected double readDouble() {
        while (true) {
            String line = input.nextLine().trim();
            try {
                double number = Double.parseDouble(line);
                if (Double.isFinite(number)) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // falls through to the retry message
            }
            System.out.print("Invalid number, try again: ");
        }
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
}
