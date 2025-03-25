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
                    .withExpiresAt(new Date(System.currentTimeMillis() + AppConstants.DateTimeConstants.JWT_TTL))
                    .sign(ServerInitializer.getJWTAlgorithm());
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while creating token", e);
        }
    }

    public static String decodeToken(String token) throws Exceptions.InvalidTokenException {
        try {
            System.out.println(token);
            JWTVerifier verifier = JWT.require(ServerInitializer.getJWTAlgorithm()).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            System.out.println(decodedJWT);
            Claim claim = decodedJWT.getClaim("username");
            System.out.println("claim is "+claim);
            return claim.asString();
        } catch (JWTVerificationException e) {
            throw new Exceptions.InvalidTokenException("Invalid bearer token!");
        }
    }
}
