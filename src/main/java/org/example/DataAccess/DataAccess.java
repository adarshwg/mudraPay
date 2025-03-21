package org.example.DataAccess;

import java.sql.*;
import java.util.ArrayList;

public class DataAccess {

    // Method to execute SELECT queries
    public static ArrayList<ArrayList<Object>> executeQuery(Connection conn, String table, String column, String condition, Object... params) throws SQLException {
        String sql = "SELECT " + column + " FROM " + table + (condition.isEmpty() ? "" : " WHERE " + condition);
        System.out.println("Executing SQL: " + sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            ArrayList<ArrayList<Object>> finalOutput = new ArrayList<>();
            while (rs.next()) {
                ArrayList<Object> row = new ArrayList<>();   // New list for each row
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnsNumber = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnsNumber; i++) {
                    row.add(rs.getObject(i));  // Add each column value to the row
                }
                finalOutput.add(row);   // Add the row to the final output list
            }

            return finalOutput;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    // Method to execute INSERT, UPDATE, or DELETE queries
    public static int executeUpdate(Connection conn, String table, String operation, String updateClause, String condition, Object... params) throws SQLException {
        if (!operation.equalsIgnoreCase("update") &&
                !operation.equalsIgnoreCase("insert") &&
                !operation.equalsIgnoreCase("delete")) {
            throw new IllegalArgumentException("Invalid SQL operation: " + operation);
        }

        String sql = "";
        String s = condition.isEmpty() ? "" : " WHERE " + condition;
        if (operation.equalsIgnoreCase("update")) {
            sql = "UPDATE " + table + " " + updateClause + s;
        } else if (operation.equalsIgnoreCase("insert")) {
            sql = "INSERT INTO " + table + " " + updateClause;
        } else if (operation.equalsIgnoreCase("delete")) {
            sql = "DELETE FROM " + table + s;
        }

        System.out.println("Executing SQL: " + sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    // Helper method to set parameters in PreparedStatement
    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Integer) {
                stmt.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof String) {
                stmt.setString(i + 1, (String) params[i]);
            } else if (params[i] instanceof Double) {
                stmt.setDouble(i + 1, (Double) params[i]);
            } else if (params[i] instanceof Float) {
                stmt.setFloat(i + 1, (Float) params[i]);
            } else if (params[i] instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) params[i]);
            } else if (params[i] instanceof Long) {
                stmt.setLong(i + 1, (Long) params[i]);
            } else {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }
}
