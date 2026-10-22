package main;

import entities.*;
import enums.*;
import java.util.ArrayList;
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
        
        Custodian custodian = new Custodian("Euan","euan@gmail.com", "pass", null);
        Custodian custodian1 = new Custodian("Bien","bien@gmail.com", "pass", null);
        system.registerCustodian(custodian);
        system.registerCustodian(custodian1);

        //EQUIPMENTS
        system.addEquipment(new Equipment("Dell Latitude Laptop", EquipmentCategory.LAPTOP, "SN-LAP-001", EquipmentCondition.GOOD, "Shelf 1"));
        system.addEquipment(new Equipment("Epson Projector X20", EquipmentCategory.PROJECTOR, "SN-PRJ-002", EquipmentCondition.GOOD, "Shelf 3"));
        system.addEquipment(new Equipment("Sony Mirrorless Camera", EquipmentCategory.CAMERA, "SN-CAM-003", EquipmentCondition.FAIR, "Cabinet B"));

        // BORROW REQUESTS
        BorrowRequest request1 = new BorrowRequest(borrower, new ArrayList<>(system.getEquipmentList().subList(0, 1)), "LABORATORY", "10/26/2026");
        BorrowRequest request2 = new BorrowRequest(borrower1, new ArrayList<>(system.getEquipmentList().subList(1, 2)), "PROJECT PRESENTATION", "10/29/2026");
        
        system.addBorrowRequest(request1);
        system.addBorrowRequest(request2);
        
        // RETURN RECORDS
        // Request 1: Equipment returned in good condition
        request1.approve();
        request1.release();
        request1.requestReturn(EquipmentCondition.GOOD);

        ReturnRecord return1 = custodian.receiveReturn(request1, EquipmentCondition.GOOD, system);

        // Request 2: Equipment returned damaged
        request2.approve();
        request2.release();
        request2.requestReturn(EquipmentCondition.DAMAGED);

        ReturnRecord return2 = custodian1.receiveReturn(request2, EquipmentCondition.DAMAGED, system);

        // DAMAGE REPORTS
        if (return2 != null) {
            DamageReport report1 = return2.triggerDamageReport(
                borrower1,
                "Projector lens is damaged and needs inspection.",
                DamageSeverity.LOW
            );

            if (report1.fileReport()) {
                system.addDamageReport(report1);
            }

            DamageReport report2 = return2.triggerDamageReport(
                borrower1,
                "Projector casing has visible scratches.",
                DamageSeverity.HIGH
            );

            if (report2.fileReport()) {
                report2.resolveReport("Inspected the casing and completed the necessary repairs.");
                system.addDamageReport(report2);
            }
        }
    }
}