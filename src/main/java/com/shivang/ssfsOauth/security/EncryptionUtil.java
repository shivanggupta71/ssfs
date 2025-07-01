package com.shivang.ssfsOauth.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EncryptionUtil {
	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
	

	private static final String KEY_FILE_PATH = System.getProperty("user.home") + "/.key_string"; // Path to the local key file
    private static SecretKey key;

    static {
        try {
            String keyString = readKeyFromFile(KEY_FILE_PATH);
            key = new SecretKeySpec(keyString.getBytes(), "AES");
        }
        catch (Exception e) {
            logger.error("Error initializing encryption key", e);
        }
    }

    private static String readKeyFromFile(String filePath) {
        try {
			return new String(Files.readAllBytes(Paths.get(filePath))).trim();
		} catch (IOException e) {
			logger.error("Error occured while reading key file..."+e.getMessage());
		}
		return filePath;
    }
	
    //private final static SecretKey key = new SecretKeySpec(keyString.getBytes(), "AES");

    public static void encryptFile(Path input, Path output) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(input.toFile());
             FileOutputStream fos = new FileOutputStream(output.toFile());
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] b = new byte[1024];
            int i;
            while ((i = fis.read(b)) != -1) {
                cos.write(b, 0, i);
            }
        }
    }

    public static void decryptFile(Path input, Path output) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        try (FileInputStream fis = new FileInputStream(input.toFile());
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(output.toFile())) {

            byte[] b = new byte[1024];
            int i;
            while ((i = cis.read(b)) != -1) {
                fos.write(b, 0, i);
            }
        }
    }
}
