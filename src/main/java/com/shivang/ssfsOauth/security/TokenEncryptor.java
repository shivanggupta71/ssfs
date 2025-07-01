package com.shivang.ssfsOauth.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TokenEncryptor {

	private static final String KEY_FILE = System.getProperty("user.home") + "/.sfss_key";

    private static SecretKey getOrCreateKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (!keyFile.exists()) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey key = keyGen.generateKey();
            Files.write(keyFile.toPath(), key.getEncoded());
            return key;
        } else {
            byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    public static void encryptToFile(String token, String tokenPath) throws Exception {
        SecretKey key = getOrCreateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(tokenPath), encrypted);
    }

    public static String decryptFromFile(String tokenPath) throws Exception {
        SecretKey key = getOrCreateKey();
        byte[] encrypted = Files.readAllBytes(Paths.get(tokenPath));
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
