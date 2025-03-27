package org.example;
import org.example.Server.ServerInitializer;
import org.example.Services.DatabaseInitializer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServerInitializer server = new ServerInitializer();
        DatabaseInitializer db = new DatabaseInitializer();
        try {
            db.initializeTables();
            server.start();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}