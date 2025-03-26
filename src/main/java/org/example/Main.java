package org.example;
import org.example.Server.ServerInitializer;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ServerInitializer server = new ServerInitializer();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}