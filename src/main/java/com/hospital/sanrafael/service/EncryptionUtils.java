package com.hospital.sanrafael.service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String SECRET = "HospitalSanRafael2026-ProyectoIntegrador";

    private static SecretKey secretKey;

    private static SecretKey getKey() {
        if (secretKey == null) {
            try {
                byte[] keyBytes = new byte[32];
                byte[] secretBytes = SECRET.getBytes("UTF-8");
                System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, 32));
                secretKey = new SecretKeySpec(keyBytes, "AES");
            } catch (Exception e) {
                throw new RuntimeException("Error initializing encryption key", e);
            }
        }
        return secretKey;
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) return plaintext;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    public static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) return ciphertext;
        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            return ciphertext;
        }
    }

    public static CipherOutputStream encryptStream(OutputStream out) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);
            out.write(iv);
            return new CipherOutputStream(out, cipher);
        } catch (Exception e) {
            throw new RuntimeException("Error creating encrypt stream", e);
        }
    }

    public static CipherInputStream decryptStream(InputStream in) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            in.read(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);
            return new CipherInputStream(in, cipher);
        } catch (Exception e) {
            throw new RuntimeException("Error creating decrypt stream", e);
        }
    }
}
