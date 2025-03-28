package org.example.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class AuthHasher {
    private static final int ITERATIONS = 65536;   // Iteration count (adjust for security)
    private static final int KEY_LENGTH = 256;     // Key length in bits

    // Generate salt
    private static byte[] getSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Hash password with PBKDF2
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = getSalt();
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Combine salt + hash and encode with Base64
        byte[] saltPlusHash = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, saltPlusHash, 0, salt.length);
        System.arraycopy(hash, 0, saltPlusHash, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(saltPlusHash);
    }

    // Verify password
    public static boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedHash = Base64.getDecoder().decode(storedHash);
        byte[] salt = new byte[16];
        byte[] hash = new byte[decodedHash.length - salt.length];

        System.arraycopy(decodedHash, 0, salt, 0, salt.length);
        System.arraycopy(decodedHash, salt.length, hash, 0, hash.length);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] calculatedHash = factory.generateSecret(spec).getEncoded();
        return MessageDigest.isEqual(hash, calculatedHash);
    }
}
