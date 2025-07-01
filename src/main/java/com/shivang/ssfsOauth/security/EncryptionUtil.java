package com.shivang.ssfsOauth.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EncryptionUtil {
	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
	
	//hardcoding secret key for testing. This should be stored or read from secrets manager or key vault
	private static String keyString = "MyVeryStrongSecretKeyForAES256!!";
	
	
    private final static SecretKey key = new SecretKeySpec(keyString.getBytes(), "AES");

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
