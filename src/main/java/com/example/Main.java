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

                // --- Bonus: Create a stored procedure for testing ---
                System.out.println("\n--- Bonus: Creating stored procedure 'getEquipmentCount' ---");
                try {
                    db.setData("DROP PROCEDURE IF EXISTS getEquipmentCount");
                    db.setData("CREATE PROCEDURE getEquipmentCount() BEGIN SELECT COUNT(*) FROM equipment; END");
                    System.out.println("Stored procedure created successfully.");
                } catch (Exception e) {
                    System.err.println("Failed to create stored procedure: " + e.getMessage());
                }

                // --- Test 14: fetchP (Prepared Statement Fetch) ---
                System.out.println("\n--- Test 14: fetchP (Prepared Statement Fetch) ---");
                Equipment equipmentP = new Equipment(568);
                equipmentP.setDb(db);
                if (equipmentP.fetchP()) {
                    System.out.println("Equipment fetched successfully using fetchP:");
                    equipmentP.printEquipment();
                } else {
                    System.out.println("No equipment found for the given ID.");
                }

                // --- Test 15: postP (Prepared Statement Insert) ---
                System.out.println("\n--- Test 15: postP (Prepared Statement Insert) ---");
                Equipment equipmentPostP = new Equipment(8888, "Prepared Vehicle", "Inserted via postP", 100);
                equipmentPostP.setDb(db);
                if (equipmentPostP.postP()) {
                    System.out.println("Equipment inserted successfully using postP.");
                }

                // --- Test 16: putP (Prepared Statement Update) ---
                System.out.println("\n--- Test 16: putP (Prepared Statement Update) ---");
                equipmentPostP.setEquipmentName("Updated Prepared Vehicle");
                if (equipmentPostP.putP()) {
                    System.out.println("Equipment updated successfully using putP.");
                }

                // --- Test 17: removeP (Prepared Statement Delete) ---
                System.out.println("\n--- Test 17: removeP (Prepared Statement Delete) ---");
                if (equipmentPostP.removeP()) {
                    System.out.println("Equipment removed successfully using removeP.");
                }

                // --- Test 18: getData with ArrayList<String> (2D Array Return) ---
                System.out.println("\n--- Test 18: getData (2D Array Return) with Prepared Parameters ---");
                ArrayList<String> queryParams = new ArrayList<>();
                queryParams.add("40");
                String[][] data2D = db.getData("SELECT * FROM equipment WHERE EquipmentCapacity > ?", queryParams);
                if (data2D != null) {
                    for (String[] row : data2D) {
                        for (String col : row) {
                            System.out.print(col + "\t");
                        }
                        System.out.println();
                    }
                } else {
                    System.out.println("getData returned null.");
                }

                // --- Test 19: executeProc (Call Stored Procedure) ---
                System.out.println("\n--- Test 19: executeProc (Call Stored Procedure) ---");
                int count = db.executeProc("getEquipmentCount", new ArrayList<>());
                System.out.println("Result from stored procedure 'getEquipmentCount': " + count);

                // --- Test 20: swapEquipment within a transaction ---
                System.out.println("\n--- Test 20: swapEquipment (Transaction) ---");

                // Step 3: Fetch both equipment objects and display initial values
                Equipment swap1 = new Equipment(568);
                swap1.setDb(db);
                Equipment swap2 = new Equipment(894);
                swap2.setDb(db);

                System.out.println("\nBefore swap:");
                if (swap1.fetchP()) {
                    System.out.println("Equipment 1 (ID 568):");
                    swap1.printEquipment();
                } else {
                    System.out.println("Equipment 1 (ID 568) not found.");
                }
                if (swap2.fetchP()) {
                    System.out.println("Equipment 2 (ID 894):");
                    swap2.printEquipment();
                } else {
                    System.out.println("Equipment 2 (ID 894) not found.");
                }

                // Step 4: Perform the swap (transaction is managed inside swapEquipment)
                System.out.println("\nSwapping equipment 568 with equipment 894...");
                boolean swapped = swap1.swapEquipment(swap2.getEquipmentId());
                if (swapped) {
                    System.out.println("Swap succeeded.");
                } else {
                    System.out.println("Swap failed or was rolled back.");
                }

                // Step 5: Fetch both again and display updated values
                System.out.println("\nAfter swap:");
                if (swap1.fetchP()) {
                    System.out.println("Equipment 1 (ID 568):");
                    swap1.printEquipment();
                }
                if (swap2.fetchP()) {
                    System.out.println("Equipment 2 (ID 894):");
                    swap2.printEquipment();
                }

                // =====================================================================
                // Tests 21-28: User authentication and authorized Equipment operations
                // =====================================================================

                // --- Test 21: Login with valid Admin credentials ---
                System.out.println("\n--- Test 21: Login – valid Admin credentials ---");
                User adminUser = db.login("admin@rit.edu", "admin123");
                if (adminUser != null) {
                    System.out.println("Admin login successful:");
                    adminUser.printUser();
                }

                // --- Test 22: Admin can perform all four authorized operations ---
                System.out.println("\n--- Test 22: Admin fetchA / postA / putA / removeA ---");
                Equipment adminEquip = new Equipment(7777, "Admin Vehicle", "Inserted by Admin", 200);
                adminEquip.setDb(db);

                System.out.print("  postA  -> ");
                System.out.println(adminEquip.postA(adminUser) ? "SUCCESS (inserted)" : "FAILED");

                System.out.print("  fetchA -> ");
                System.out.println(adminEquip.fetchA(adminUser) ? "SUCCESS" : "FAILED");

                adminEquip.setEquipmentName("Admin Vehicle Updated");
                System.out.print("  putA   -> ");
                System.out.println(adminEquip.putA(adminUser) ? "SUCCESS (updated)" : "FAILED");

                System.out.print("  removeA -> ");
                System.out.println(adminEquip.removeA(adminUser) ? "SUCCESS (deleted)" : "FAILED");

                // --- Test 23: Login with valid Editor credentials ---
                System.out.println("\n--- Test 23: Login – valid Editor credentials ---");
                User editorUser = db.login("editor@rit.edu", "editor123");
                if (editorUser != null) {
                    System.out.println("Editor login successful:");
                    editorUser.printUser();
                }

                // --- Test 24: Editor can fetch/put/post but NOT remove ---
                System.out.println("\n--- Test 24: Editor fetchA / postA / putA / removeA ---");
                Equipment editorEquip = new Equipment(6666, "Editor Vehicle", "Inserted by Editor", 80);
                editorEquip.setDb(db);

                System.out.print("  postA   -> ");
                System.out.println(editorEquip.postA(editorUser) ? "SUCCESS (inserted)" : "FAILED");

                System.out.print("  fetchA  -> ");
                System.out.println(editorEquip.fetchA(editorUser) ? "SUCCESS" : "FAILED");

                editorEquip.setEquipmentName("Editor Vehicle Updated");
                System.out.print("  putA    -> ");
                System.out.println(editorEquip.putA(editorUser) ? "SUCCESS (updated)" : "FAILED");

                System.out.print("  removeA -> ");
                boolean editorRemove = editorEquip.removeA(editorUser);
                System.out.println(editorRemove ? "SUCCESS" : "DENIED (expected for Editor)");

                // Clean up editor's test record using a direct method
                if (!editorRemove) editorEquip.removeP();

                // --- Test 25: Login with valid General credentials ---
                System.out.println("\n--- Test 25: Login – valid General credentials ---");
                User generalUser = db.login("user@rit.edu", "user123");
                if (generalUser != null) {
                    System.out.println("General login successful:");
                    generalUser.printUser();
                }

                // --- Test 26: General can only fetch ---
                System.out.println("\n--- Test 26: General fetchA / postA / putA / removeA ---");
                Equipment generalEquip = new Equipment(568);
                generalEquip.setDb(db);

                System.out.print("  fetchA  -> ");
                System.out.println(generalEquip.fetchA(generalUser) ? "SUCCESS" : "FAILED");

                System.out.print("  postA   -> ");
                Equipment generalPost = new Equipment(5555, "General Vehicle", "Should not insert", 10);
                generalPost.setDb(db);
                System.out.println(generalPost.postA(generalUser) ? "SUCCESS" : "DENIED (expected for General)");

                System.out.print("  putA    -> ");
                System.out.println(generalEquip.putA(generalUser) ? "SUCCESS" : "DENIED (expected for General)");

                System.out.print("  removeA -> ");
                System.out.println(generalEquip.removeA(generalUser) ? "SUCCESS" : "DENIED (expected for General)");

                // --- Test 27: Unauthenticated user is denied all operations ---
                System.out.println("\n--- Test 27: Unauthenticated user denied all operations ---");
                User unauthUser = new User("ghost@rit.edu", "nope");
                Equipment unauthEquip = new Equipment(568);
                unauthEquip.setDb(db);

                System.out.print("  fetchA with unauthenticated user -> ");
                System.out.println(unauthEquip.fetchA(unauthUser) ? "SUCCESS" : "DENIED (expected)");

                // --- Test 28: Login with invalid credentials ---
                System.out.println("\n--- Test 28: Login with invalid credentials ---");
                User invalidUser = db.login("nobody@rit.edu", "wrongpassword");
                System.out.println(invalidUser == null ? "Login correctly returned null" : "Unexpected success");

                if (db.close()) {
                    System.out.println("\nSuccessfully closed the database connection!");
                }

                // --- Test 29: Repeated failed logins terminate the application ---
                // NOTE: A new DB connection is needed because we closed it above.
                // This test will call System.exit(1) on the 3rd failed attempt.
                System.out.println("\n--- Test 29: Repeated failed logins (app terminates after 3rd failure) ---");
                MySQLDatabase db2 = new MySQLDatabase(
                        System.getenv("DB_HOST"),
                        Integer.parseInt(System.getenv("DB_PORT")),
                        System.getenv("DB_NAME"),
                        System.getenv("DB_USER"),
                        System.getenv("DB_PASSWORD"));
                db2.connect();
                db2.login("nobody@rit.edu", "wrong1");  // Failure 2 (1 already counted above)
                db2.login("nobody@rit.edu", "wrong2");  // Failure 3 – app terminates here
                System.out.println("This line should never be reached.");

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
