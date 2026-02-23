package com.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String dbHost = System.getenv("DB_HOST");
        int dbPort = Integer.parseInt(System.getenv("DB_PORT"));
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        MySQLDatabase db = new MySQLDatabase(dbHost, dbPort, dbName, dbUser, dbPassword);

        try {
            if (db.connect()) {
                System.out.println("Successfully connected to the database!");

                // --- Test 1: ID-only constructor + fetch ---
                System.out.println("\n--- Test 1: Fetch using ID-only constructor ---");
                Equipment equipment1 = new Equipment(568);
                equipment1.setDb(db);
                if (equipment1.fetch()) {
                    System.out.println("Equipment fetched successfully:");
                    equipment1.printEquipment();
                } else {
                    System.out.println("No equipment found for the given ID.");
                }

                // --- Test 2: All-attributes constructor + post (insert) ---
                System.out.println("\n--- Test 2: Insert using all-attributes constructor ---");
                Equipment equipment2 = new Equipment(9999, "Test Vehicle", "A test entry", 50);
                equipment2.setDb(db);
                if (equipment2.post()) {
                    System.out.println("Equipment inserted successfully:");
                    equipment2.printEquipment();
                }

                // --- Test 3: Fetch the newly inserted record to verify ---
                System.out.println("\n--- Test 3: Fetch the newly inserted record ---");
                Equipment equipment3 = new Equipment(9999);
                equipment3.setDb(db);
                if (equipment3.fetch()) {
                    System.out.println("Newly inserted equipment fetched:");
                    equipment3.printEquipment();
                }

                // --- Test 4: Update (put) the inserted record ---
                System.out.println("\n--- Test 4: Update the inserted record ---");
                equipment3.setEquipmentName("Updated Vehicle");
                equipment3.setEquipmentDescription("Updated description");
                equipment3.setEquipmentCapacity(75);
                if (equipment3.put()) {
                    System.out.println("Equipment updated successfully. New values:");
                    equipment3.printEquipment();
                }

                // --- Test 5: Fetch again to confirm update ---
                System.out.println("\n--- Test 5: Fetch to confirm update ---");
                Equipment equipment4 = new Equipment(9999);
                equipment4.setDb(db);
                if (equipment4.fetch()) {
                    System.out.println("Updated equipment fetched:");
                    equipment4.printEquipment();
                }

                // --- Test 6: Delete (remove) the test record ---
                System.out.println("\n--- Test 6: Remove the test record ---");
                if (equipment4.remove()) {
                    System.out.println("Equipment with ID 9999 removed successfully.");
                }

                // --- Test 7: Default constructor + setters ---
                System.out.println("\n--- Test 7: Default constructor with setters ---");
                Equipment equipment5 = new Equipment();
                equipment5.setDb(db);
                equipment5.setEquipmentId(894);
                if (equipment5.fetch()) {
                    System.out.println("Equipment fetched using default constructor + setter:");
                    equipment5.printEquipment();
                }

                // --- Test 8: Intentional Error (SQL Error) ---
                System.out.println("\n--- Test 8: Intentional Error (SQL Error) ---");
                try {
                    System.out.println("Attempting to query a non-existent table...");
                    db.getData("SELECT * FROM non_existent_table");
                } catch (DLException e) {
                    System.out.println("Caught expected DLException for Test 8:");
                    System.err.println("[USER MESSAGE] " + e.getMessage());
                }

                // --- Test 9: New getData with boolean (include headers) ---
                System.out.println("\n--- Test 9: getData with headers = true ---");
                ArrayList<ArrayList<String>> dataWithHeaders = db.getData("SELECT * FROM equipment LIMIT 3", true);
                for (ArrayList<String> row : dataWithHeaders) {
                    System.out.println(row);
                }

                // --- Test 10: New getData with boolean (exclude headers) ---
                System.out.println("\n--- Test 10: getData with headers = false ---");
                ArrayList<ArrayList<String>> dataNoHeaders = db.getData("SELECT * FROM equipment LIMIT 3", false);
                for (ArrayList<String> row : dataNoHeaders) {
                    System.out.println(row);
                }

                // --- Test 11: printDatabaseInfo ---
                System.out.println("\n--- Test 11: printDatabaseInfo ---");
                db.printDatabaseInfo();

                // --- Test 12: printTableInfo ---
                System.out.println("\n--- Test 12: printTableInfo ---");
                db.printTableInfo("equipment");

                // --- Test 13: printResultInfo ---
                System.out.println("\n--- Test 13: printResultInfo ---");
                db.printResultInfo("SELECT * FROM equipment WHERE EquipmentCapacity > 10");

                if (db.close()) {
                    System.out.println("\nSuccessfully closed the database connection!");
                }
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (DLException e) {
            // This is where the innocuous message is presented to the user
            System.err.println("\n[ERROR] " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
        }
    }
}
