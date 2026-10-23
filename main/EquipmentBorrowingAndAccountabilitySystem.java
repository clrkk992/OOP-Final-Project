package main;

import entities.*;
import enums.*;
import java.util.Scanner;
import service.EquipmentInventorySystem;

/*
 * ===== MAIN RUNNER =====
 *  - Entry point of the program. Seeds mock data and invokes Console.runSystem().
 */

public class EquipmentBorrowingAndAccountabilitySystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EquipmentInventorySystem inventorySystem = new EquipmentInventorySystem();

        // Seed sample test accounts and equipment
        seedMockData(inventorySystem);

        // Start the console runner
        Console.runSystem(scanner, inventorySystem);

        scanner.close();
    }

    private static void seedMockData(EquipmentInventorySystem system) {
        //USERS
        Borrower borrower = new Borrower("Ryan", "ryan@gmail.com", "pass");
        Borrower borrower1 = new Borrower("Clark", "clark@gmail.com", "pass");
        system.registerBorrower(borrower);
        system.registerBorrower(borrower1);
        
        Custodian custodian = new Custodian("Euan","euan@gmail.com", "pass");
        Custodian custodian1 = new Custodian("Bien","bien@gmail.com", "pass");
        system.registerCustodian(custodian);
        system.registerCustodian(custodian1);

        //EQUIPMENTS
        system.addEquipment(new Equipment("Dell Latitude Laptop", EquipmentCategory.LAPTOP, "SN-LAP-001", EquipmentCondition.GOOD, "Shelf 1"));
        system.addEquipment(new Equipment("Epson Projector X20", EquipmentCategory.PROJECTOR, "SN-PRJ-002", EquipmentCondition.GOOD, "Shelf 3"));
        system.addEquipment(new Equipment("Sony Mirrorless Camera", EquipmentCategory.CAMERA, "SN-CAM-003", EquipmentCondition.FAIR, "Cabinet B"));
    }
}