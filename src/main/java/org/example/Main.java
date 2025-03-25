package org.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.Server.ServerInitializer;
import org.example.utils.Token;

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