package com.example;

public class Main {
    public static void main(String[] args) {
        String dbHost = System.getenv("DB_HOST");
        int dbPort = Integer.parseInt(System.getenv("DB_PORT"));
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        MySQLDatabase db = new MySQLDatabase(dbHost, dbPort, dbName, dbUser, dbPassword);

        if (db.connect()) {
            System.out.println("Successfully connected to the database!");
            if (db.close()) {
                System.out.println("Successfully closed the database connection!");
            } else {
                System.out.println("Failed to close the database connection.");
            }
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}
