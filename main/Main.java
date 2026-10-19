package main;

import java.util.Scanner;
import entities.*;
import enums.*;
import service.EquipmentInventorySystem;

/*
 * ===== MAIN RUNNER =====
 *  - Entry point of the program. Seeds mock data and invokes Console.runSystem().
 */

public class Main {
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
        system.addEquipment(new Equipment("Dell Latitude Laptop", EquipmentCategory.LAPTOP, "SN-LAP-001", EquipmentCondition.GOOD, "Shelf 1"));
        system.addEquipment(new Equipment("Epson Projector X20", EquipmentCategory.PROJECTOR, "SN-PRJ-002", EquipmentCondition.GOOD, "Shelf 3"));
        system.addEquipment(new Equipment("Sony Mirrorless Camera", EquipmentCategory.CAMERA, "SN-CAM-003", EquipmentCondition.FAIR, "Cabinet B"));
    }
}