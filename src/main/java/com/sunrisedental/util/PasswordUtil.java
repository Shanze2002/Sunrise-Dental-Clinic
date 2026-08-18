package com.sunrisedental.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility: PasswordUtil
 * Handles secure password hashing using SHA-256 with cryptographically generated salts.
 */
public class PasswordUtil {

    private static final String DEFAULT_SALT = "SDC_SALT_2026";

    /**
     * Generate a new random salt string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash password with given salt using SHA-256
     */
    public static String hashPassword(String password, String salt) {
        if (password == null) password = "";
        if (salt == null || salt.trim().isEmpty()) salt = DEFAULT_SALT;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String combined = password + salt;
            byte[] hashBytes = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not available", e);
        }
    }

    /**
     * Verify if plain text password matches stored hash and salt
     */
    public static boolean verifyPassword(String plainPassword, String storedHash, String salt) {
        if (plainPassword == null || storedHash == null) return false;
        String calculatedHash = hashPassword(plainPassword, salt);
        return calculatedHash.equalsIgnoreCase(storedHash);
    }
}
