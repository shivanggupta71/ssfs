package com.shivang.ssfsOauth.security;


import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.*;


public class EncryptionUtilTest {

	private static Path inputFile;
    private static Path encryptedFile;
    private static Path decryptedFile;
    private static final String TEST_CONTENT = "This is a secret message for testing AES encryption.";

    @BeforeEach
    void setup() throws IOException {
        inputFile = Files.createTempFile("sfss_input", ".txt");
        encryptedFile = Files.createTempFile("sfss_encrypted", ".enc");
        decryptedFile = Files.createTempFile("sfss_decrypted", ".txt");

        Files.writeString(inputFile, TEST_CONTENT);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(inputFile);
        Files.deleteIfExists(encryptedFile);
        Files.deleteIfExists(decryptedFile);
    }

    @Test
    void testEncryptAndDecryptFile() {
        try {
            // Encrypt and then decrypt
            EncryptionUtil.encryptFile(inputFile, encryptedFile);
            EncryptionUtil.decryptFile(encryptedFile, decryptedFile);

            String decryptedContent = Files.readString(decryptedFile);
            assertEquals(TEST_CONTENT, decryptedContent, "Decrypted content should match original input");
        } catch (Exception e) {
            fail("Encryption/decryption failed: " + e.getMessage());
        }
    }

	/*
	 * @Test void testEncryptedFileIsDifferentFromOriginal() throws Exception {
	 * EncryptionUtil.encryptFile(inputFile, encryptedFile); String encryptedContent
	 * = Files.readString(encryptedFile); assertNotEquals(TEST_CONTENT,
	 * encryptedContent, "Encrypted content should not be the same as plaintext"); }
	 */
}
