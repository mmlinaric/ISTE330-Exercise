package com.example;

import java.util.ArrayList;

public class Equipment {
    private int equipmentId;
    private String equipmentName;
    private String equipmentDescription;
    private int equipmentCapacity;

    private MySQLDatabase db;

    // Default constructor
    public Equipment() {
    }

    // Constructor that accepts only equipmentId
    public Equipment(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    // Constructor that accepts all attributes
    public Equipment(int equipmentId, String equipmentName, String equipmentDescription, int equipmentCapacity) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentDescription = equipmentDescription;
        this.equipmentCapacity = equipmentCapacity;
    }

    // Getters
    public int getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public int getEquipmentCapacity() {
        return equipmentCapacity;
    }

    // Setters
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

    public void setEquipmentCapacity(int equipmentCapacity) {
        this.equipmentCapacity = equipmentCapacity;
    }

    public void setDb(MySQLDatabase db) {
        this.db = db;
    }

    // fetch: retrieves database values for the object's equipmentId and updates attributes
    public boolean fetch() throws DLException {
        try {
            String sql = "SELECT EquipID, EquipmentName, EquipmentDescription, EquipmentCapacity FROM equipment WHERE EquipID = " + this.equipmentId;
            ArrayList<ArrayList<String>> result = db.getData(sql);

            if (result.size() < 2) {
                System.out.println("No equipment found with ID: " + this.equipmentId);
                return false;
            }

            ArrayList<String> row = result.get(1);
            this.equipmentId = Integer.parseInt(row.get(0));
            this.equipmentName = row.get(1);
            this.equipmentDescription = row.get(2);
            this.equipmentCapacity = Integer.parseInt(row.get(3));
            return true;
        } catch (Exception e) {
            throw new DLException(e, "Method: fetch", "EquipID: " + this.equipmentId);
        }
    }

    // put: updates the database row for this equipmentId with current attribute values
    public boolean put() throws DLException {
        try {
            String sql = "UPDATE equipment SET EquipmentName = '" + this.equipmentName
                    + "', EquipmentDescription = '" + this.equipmentDescription
                    + "', EquipmentCapacity = " + this.equipmentCapacity
                    + " WHERE EquipID = " + this.equipmentId;
            return db.setData(sql);
        } catch (Exception e) {
            throw new DLException(e, "Method: put", "EquipID: " + this.equipmentId);
        }
    }

    // post: inserts a new row into the database using the object's attribute values
    public boolean post() throws DLException {
        try {
            String sql = "INSERT INTO equipment (EquipID, EquipmentName, EquipmentDescription, EquipmentCapacity) VALUES ("
                    + this.equipmentId + ", '"
                    + this.equipmentName + "', '"
                    + this.equipmentDescription + "', "
                    + this.equipmentCapacity + ")";
            return db.setData(sql);
        } catch (Exception e) {
            throw new DLException(e, "Method: post", "EquipID: " + this.equipmentId);
        }
    }

    // remove: deletes the database row corresponding to the object's equipmentId
    public boolean remove() throws DLException {
        try {
            String sql = "DELETE FROM equipment WHERE EquipID = " + this.equipmentId;
            return db.setData(sql);
        } catch (Exception e) {
            throw new DLException(e, "Method: remove", "EquipID: " + this.equipmentId);
        }
    }

    // fetchP: retrieves database values using prepared statements
    public boolean fetchP() throws DLException {
        try {
            String sql = "SELECT EquipID, EquipmentName, EquipmentDescription, EquipmentCapacity FROM equipment WHERE EquipID = ?";
            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(this.equipmentId));
            
            String[][] result = db.getData(sql, values);

            if (result == null || result.length < 2) {
                System.out.println("No equipment found with ID: " + this.equipmentId);
                return false;
            }

            String[] row = result[1];
            this.equipmentId = Integer.parseInt(row[0]);
            this.equipmentName = row[1];
            this.equipmentDescription = row[2];
            this.equipmentCapacity = Integer.parseInt(row[3]);
            return true;
        } catch (Exception e) {
            throw new DLException(e, "Method: fetchP", "EquipID: " + this.equipmentId);
        }
    }

    // putP: updates the database row using prepared statements
    public boolean putP() throws DLException {
        try {
            String sql = "UPDATE equipment SET EquipmentName = ?, EquipmentDescription = ?, EquipmentCapacity = ? WHERE EquipID = ?";
            ArrayList<String> values = new ArrayList<>();
            values.add(this.equipmentName);
            values.add(this.equipmentDescription);
            values.add(String.valueOf(this.equipmentCapacity));
            values.add(String.valueOf(this.equipmentId));
            return db.setData(sql, values);
        } catch (Exception e) {
            throw new DLException(e, "Method: putP", "EquipID: " + this.equipmentId);
        }
    }

    // postP: inserts a new row using prepared statements
    public boolean postP() throws DLException {
        try {
            String sql = "INSERT INTO equipment (EquipID, EquipmentName, EquipmentDescription, EquipmentCapacity) VALUES (?, ?, ?, ?)";
            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(this.equipmentId));
            values.add(this.equipmentName);
            values.add(this.equipmentDescription);
            values.add(String.valueOf(this.equipmentCapacity));
            return db.setData(sql, values);
        } catch (Exception e) {
            throw new DLException(e, "Method: postP", "EquipID: " + this.equipmentId);
        }
    }

    // removeP: deletes the database row using prepared statements
    public boolean removeP() throws DLException {
        try {
            String sql = "DELETE FROM equipment WHERE EquipID = ?";
            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(this.equipmentId));
            return db.setData(sql, values);
        } catch (Exception e) {
            throw new DLException(e, "Method: removeP", "EquipID: " + this.equipmentId);
        }
    }

    // Utility method to display equipment values to the user
    public void printEquipment() {
        System.out.println("  Equipment ID:          " + equipmentId);
        System.out.println("  Equipment Name:        " + equipmentName);
        System.out.println("  Equipment Description: " + equipmentDescription);
        System.out.println("  Equipment Capacity:    " + equipmentCapacity);
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentId=" + equipmentId +
                ", equipmentName='" + equipmentName + '\'' +
                ", equipmentDescription='" + equipmentDescription + '\'' +
                ", equipmentCapacity=" + equipmentCapacity +
                '}';
    }
}
