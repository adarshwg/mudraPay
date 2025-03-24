package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.Server.ServerInitializer;
import org.example.utils.Token;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ServerInitializer server = new ServerInitializer();
        String token = Token.generateToken("ad123");
        System.out.println(Token.decodeToken(token));
        System.out.println();
        Dotenv dotenv = Dotenv.configure().directory("src/main/resources").load();
        // Access the environment variable
        String jwtSecret = dotenv.get("JWT_SECRET");
        System.out.println("JWT_SECRET: " + jwtSecret);

        try {
            System.out.println(System.getenv("JWT_SECRET"));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}