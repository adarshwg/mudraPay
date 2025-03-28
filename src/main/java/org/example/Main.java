package org.example;
import org.example.Server.ServerInitializer;
import org.example.Services.DatabaseInitializer;
import org.example.utils.AuthHasher;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ServerInitializer server = new ServerInitializer();
        DatabaseInitializer db = new DatabaseInitializer();
        System.out.println(AuthHasher.verifyPassword("New02@","2VN4jYbiFGmvJJykNAGKN8NVqmJwDNHqrBkmFdI5kzb6uk8Zi4cOEJRSjOF/CLjh"));
        try {
            db.initializeTables();
            server.start();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}