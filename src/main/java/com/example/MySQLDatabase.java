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
    private ResultSet rs;

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
        return getData(sql, true);
    }

    public ArrayList<ArrayList<String>> getData(String sql, boolean includeColumnNames) throws DLException {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            this.rs = stmt.executeQuery();
            ArrayList<ArrayList<String>> result = fetch(includeColumnNames);
            this.rs.close();
            stmt.close();
            return result;
        } catch (SQLException e) {
            throw new DLException(e, "Query: " + sql);
        }
    }

    public ArrayList<ArrayList<String>> fetch(boolean includeColumnNames) throws DLException {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        try {
            ResultSetMetaData metaData = this.rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (includeColumnNames) {
                ArrayList<String> headers = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    headers.add(metaData.getColumnName(i));
                }
                result.add(headers);
            }

            while (this.rs.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(this.rs.getString(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new DLException(e, "Error fetching data from ResultSet");
        }
        return result;
    }

    public void printDatabaseInfo() throws DLException {
        try {
            java.sql.DatabaseMetaData dbmd = this.connection.getMetaData();
            System.out.println("Database Product Name: " + dbmd.getDatabaseProductName());
            System.out.println("Database Product Version: " + dbmd.getDatabaseProductVersion());
            System.out.println("Driver Name: " + dbmd.getDriverName());
            System.out.println("Driver Version: " + dbmd.getDriverVersion());

            System.out.println("\nTables:");
            ResultSet tables = dbmd.getTables(null, null, "%", null);
            while (tables.next()) {
                System.out.println(" - " + tables.getString("TABLE_NAME") + " (Type: " + tables.getString("TABLE_TYPE") + ")");
            }
            tables.close();

            System.out.println("\nSupport:");
            System.out.println(" - Supports Group By: " + dbmd.supportsGroupBy());
            System.out.println(" - Supports Outer Joins: " + dbmd.supportsOuterJoins());
            System.out.println(" - Supports Statement Pooling: " + dbmd.supportsStatementPooling());
        } catch (SQLException e) {
            throw new DLException(e, "Error printing database info");
        }
    }

    public void printTableInfo(String tableName) throws DLException {
        try {
            java.sql.DatabaseMetaData dbmd = this.connection.getMetaData();
            System.out.println("Table Information for: " + tableName);

            ResultSet columns = dbmd.getColumns(null, null, tableName, null);
            int columnCount = 0;
            System.out.println("Columns:");
            while (columns.next()) {
                columnCount++;
                System.out.println(" - Name: " + columns.getString("COLUMN_NAME") + ", Type: " + columns.getString("TYPE_NAME"));
            }
            columns.close();
            System.out.println("Column Count: " + columnCount);

            ResultSet pks = dbmd.getPrimaryKeys(null, null, tableName);
            System.out.println("Primary Keys:");
            while (pks.next()) {
                System.out.println(" - " + pks.getString("COLUMN_NAME"));
            }
            pks.close();

            // Bonus: Exact number of rows
            String countSql = "SELECT COUNT(*) FROM " + tableName;
            PreparedStatement stmt = this.connection.prepareStatement(countSql);
            ResultSet rsCount = stmt.executeQuery();
            if (rsCount.next()) {
                System.out.println("Row Count (Bonus): " + rsCount.getInt(1));
            }
            rsCount.close();
            stmt.close();

        } catch (SQLException e) {
            throw new DLException(e, "Error printing table info for: " + tableName);
        }
    }

    public void printResultInfo(String sql) throws DLException {
        try {
            System.out.println("Result Set Information for Query: " + sql);
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println("Column Count: " + columnCount);

            System.out.println("Columns:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(" - Name: " + rsmd.getColumnName(i) 
                    + ", Type: " + rsmd.getColumnTypeName(i) 
                    + ", Searchable (Possible WHERE clause): " + rsmd.isSearchable(i));
            }

            // Bonus: Number of rows in result set
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            System.out.println("Row Count in Result Set (Bonus): " + rowCount);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new DLException(e, "Error printing result info for query: " + sql);
        }
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

    /**
     * Prepares an SQL statement and binds the values.
     * @param sql The SQL statement string.
     * @param values The values to bind to the statement.
     * @return A PreparedStatement object.
     * @throws DLException if preparation or binding fails.
     */
    public PreparedStatement prepare(String sql, ArrayList<String> values) throws DLException {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(sql);
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }
            return stmt;
        } catch (SQLException e) {
            throw new DLException(e, "Prepare failed for: " + sql);
        }
    }

    /**
     * Executes a query with bound values and returns results as a 2D array.
     * @param sql The SQL statement string.
     * @param values The values to bind.
     * @return A 2D array of strings where the first row is column names, or null on failure.
     */
    public String[][] getData(String sql, ArrayList<String> values) {
        try {
            PreparedStatement stmt = prepare(sql, values);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            ArrayList<String[]> rows = new ArrayList<>();

            // Add headers
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = metaData.getColumnName(i);
            }
            rows.add(headers);

            // Add data rows
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                rows.add(row);
            }

            rs.close();
            stmt.close();

            return rows.toArray(new String[0][]);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Executes an update/insert/delete with bound values.
     * @param sql The SQL statement string.
     * @param values The values to bind.
     * @return true if successful, false otherwise.
     * @throws DLException if something goes wrong.
     */
    public boolean setData(String sql, ArrayList<String> values) throws DLException {
        try {
            PreparedStatement stmt = prepare(sql, values);
            int result = stmt.executeUpdate();
            stmt.close();
            return result >= 0;
        } catch (SQLException e) {
            throw new DLException(e, "setData failed for: " + sql);
        }
    }

    /**
     * Executes a stored procedure that returns a single integer.
     * @param procName The name of the stored procedure.
     * @param values The values to bind.
     * @return The integer result from the procedure.
     * @throws DLException if something goes wrong.
     */
    public int executeProc(String procName, ArrayList<String> values) throws DLException {
        StringBuilder sql = new StringBuilder("{call ").append(procName).append("(");
        for (int i = 0; i < values.size(); i++) {
            sql.append("?");
            if (i < values.size() - 1) sql.append(",");
        }
        sql.append(")}");

        try (java.sql.CallableStatement stmt = this.connection.prepareCall(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }

            // Execute and try to get a single integer result
            boolean hasResults = stmt.execute();
            if (hasResults) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DLException(e, "executeProc failed for: " + procName);
        }
    }
}
