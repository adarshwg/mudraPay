package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.example.Server.ServerInitializer;

import java.util.Date;

public class Token {

    public static String generateToken(String username) {
        try {
            return JWT.create()
                    .withSubject("user")
                    .withClaim("username", username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
                    .sign(ServerInitializer.getJWTAlgorithm());
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while creating token", e);
        }
    }

    public static String decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(ServerInitializer.getJWTAlgorithm()).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Claim claim = decodedJWT.getClaim("username");
            return claim.asString();
        } catch (JWTVerificationException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
    }
}
