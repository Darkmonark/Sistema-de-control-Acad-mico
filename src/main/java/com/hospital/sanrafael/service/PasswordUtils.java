package com.hospital.sanrafael.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes());

            byte[] combined = new byte[salt.length + hashed.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashed, 0, combined, salt.length, hashed.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            byte[] storedHashed = new byte[combined.length - salt.length];
            System.arraycopy(combined, salt.length, storedHashed, 0, storedHashed.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] computedHash = md.digest(password.getBytes());

            return MessageDigest.isEqual(storedHashed, computedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
