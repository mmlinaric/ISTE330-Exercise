package com.example;

import java.security.MessageDigest;
import java.util.ArrayList;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private String organizationUnit;
    private boolean authenticated;

    public User() {}

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String firstName, String lastName, String password, String role, String organizationUnit) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.organizationUnit = organizationUnit;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getOrganizationUnit() { return organizationUnit; }
    public boolean isAuthenticated() { return authenticated; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setOrganizationUnit(String organizationUnit) { this.organizationUnit = organizationUnit; }

    /**
     * Hashes a plaintext password using SHA-256 and returns a 64-character hex string.
     */
    private static String hashPassword(String password) throws DLException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new DLException(e, "Method: hashPassword");
        }
    }

    /**
     * Verifies the user credentials against the database.
     * On success, fills in firstName, lastName, role, and organizationUnit from the DB,
     * sets authenticated = true, and returns true. Returns false if credentials are invalid.
     */
    public boolean authenticate(MySQLDatabase db) throws DLException {
        String hashedPassword = hashPassword(this.password);

        String sql = "SELECT u.UserId, u.FirstName, u.LastName, r.RoleName, u.OrganizationUnit " +
                     "FROM `user` u JOIN `userrole` r ON u.RoleID = r.RoleID " +
                     "WHERE u.UserId = ? AND u.Password = ?";
        ArrayList<String> values = new ArrayList<>();
        values.add(this.userId);
        values.add(hashedPassword);

        String[][] result = db.getData(sql, values);

        if (result == null || result.length < 2) {
            this.authenticated = false;
            return false;
        }

        String[] row = result[1];
        this.userId = row[0];
        this.firstName = row[1];
        this.lastName = row[2];
        this.role = row[3];
        this.organizationUnit = row[4];
        this.authenticated = true;
        return true;
    }

    public void printUser() {
        System.out.println("  User ID:           " + userId);
        System.out.println("  First Name:        " + firstName);
        System.out.println("  Last Name:         " + lastName);
        System.out.println("  Role:              " + role);
        System.out.println("  Organization Unit: " + organizationUnit);
    }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', firstName='" + firstName +
               "', lastName='" + lastName + "', role='" + role +
               "', organizationUnit='" + organizationUnit + "', authenticated=" + authenticated + '}';
    }
}
