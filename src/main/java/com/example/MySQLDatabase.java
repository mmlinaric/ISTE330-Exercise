package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLDatabase {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private Connection connection;

    public MySQLDatabase(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean connect() throws DLException {
        try {
            String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
            this.connection = DriverManager.getConnection(url, this.user, this.password);
            return true;
        } catch (SQLException e) {
            throw new DLException(e);
        }
    }

    public boolean close() throws DLException {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DLException(e);
        }
    }

    public ArrayList<ArrayList<String>> getData(String sql) throws DLException {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // First row: column names
            ArrayList<String> headers = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                headers.add(metaData.getColumnName(i));
            }
            result.add(headers);

            // Data rows
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                result.add(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new DLException(e, "Query: " + sql);
        }
        return result;
    }

    public boolean setData(String sql) throws DLException {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            throw new DLException(e, "Query: " + sql);
        }
    }
}
