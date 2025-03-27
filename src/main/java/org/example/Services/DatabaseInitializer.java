package org.example.Services;

import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {
    private final Connection conn;

    public DatabaseInitializer() {
        this.conn = new ConnectDB().getConnection();
    }

    // Method to initialize all tables
    public void initializeTables() throws SQLException {
        createUserTable();
        createWalletTable();
        createTransactionTable();
        System.out.println("Database initialized successfully!");
    }

    // Method to create User table
    private void createUserTable() throws SQLException {
        String definition = "(username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "mudraPin TEXT NOT NULL)" ;
        DataAccess.executeUpdate(this.conn, "user", "create", definition, "");
        System.out.println("User table created successfully.");
    }

    // Method to create Wallet table
    private void createWalletTable() throws SQLException {
        String definition = "(username TEXT NOT NULL UNIQUE, " +
                "balance INTEGER NOT NULL)";
        DataAccess.executeUpdate(this.conn, "wallet", "create", definition, "");
        System.out.println("Wallet table created successfully.");
    }

    // Method to create Transaction table
    private void createTransactionTable() throws SQLException {
        String definition = """
                (               transaction_id TEXT PRIMARY KEY NOT NULL,
                                sender TEXT NOT NULL,
                                receiver TEXT NOT NULL,
                                amount INTEGER NOT NULL,
                                epochTime BIGINT NOT NULL
                                )""";
        DataAccess.executeUpdate(this.conn, "transactions", "create", definition, "");
        System.out.println("Transaction table created successfully.");
    }

    // Method to close the connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
