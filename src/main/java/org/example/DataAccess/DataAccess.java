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
                ArrayList<Object> row = new ArrayList<>();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnsNumber = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnsNumber; i++) {
                    row.add(rs.getObject(i));
                }
                finalOutput.add(row);
            }

            return finalOutput;

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    // Method to execute INSERT, UPDATE, DELETE, and CREATE queries
    public static int executeUpdate(Connection conn, String table, String operation, String clause, String condition, Object... params) throws SQLException {
        String sql;

        // Handle different operations, including CREATE TABLE
        switch (operation.toLowerCase()) {
            case "insert":
                sql = "INSERT INTO " + table + " " + clause;
                break;
            case "update":
                sql = "UPDATE " + table + " " + clause + (condition.isEmpty() ? "" : " WHERE " + condition);
                break;
            case "delete":
                sql = "DELETE FROM " + table + (condition.isEmpty() ? "" : " WHERE " + condition);
                break;
            case "create":
                sql = "CREATE TABLE IF NOT EXISTS " + table + " " + clause;  // Backward-compatible CREATE
                break;
            default:
                throw new IllegalArgumentException("Invalid SQL operation: " + operation);
        }

        System.out.println("Executing SQL: " + sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    // Helper method to create tables with dynamic SQL
    public static void createTable(Connection conn, String tableName, String tableDefinition) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " " + tableDefinition;
        System.out.println("Creating table: " + sql);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
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
