package com.shivang.ssfsOauth.security;


import org.junit.jupiter.api.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionUtilTest {

    private static Path tempHomeDir;
    private static Path keyFile;
    private static Path inputFile;
    private static Path encryptedFile;
    private static Path decryptedFile;
    private static final String TEST_CONTENT = "This is a secret message for testing AES encryption.";
    private static final String TEST_KEY_STRING = "12345678901234561234567890123456"; // 32-byte key

    private static String originalUserHome;

    @BeforeAll
    static void beforeAll() throws Exception {
        // Save original user.home
        originalUserHome = System.getProperty("user.home");

        // Create a temp directory to act as user.home
        tempHomeDir = Files.createTempDirectory("test_home");

        // Write the AES key to .key_string file
        keyFile = tempHomeDir.resolve(".key_string");
        Files.writeString(keyFile, TEST_KEY_STRING);

        // Override user.home so EncryptionUtil loads the test key
        System.setProperty("user.home", tempHomeDir.toString());

        // Force re-initialization of EncryptionUtil.key using reflection
        resetEncryptionUtilKey(TEST_KEY_STRING);
    }

    @AfterAll
    static void afterAll() throws IOException {
        // Restore original user.home
        System.setProperty("user.home", originalUserHome);

        // Cleanup key file and temp directory
        Files.deleteIfExists(keyFile);
        Files.deleteIfExists(tempHomeDir);
    }

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
            EncryptionUtil.encryptFile(inputFile, encryptedFile);
            EncryptionUtil.decryptFile(encryptedFile, decryptedFile);

            String decryptedContent = Files.readString(decryptedFile);
            assertEquals(TEST_CONTENT, decryptedContent, "Decrypted content should match original input");
        } catch (Exception e) {
            fail("Encryption/decryption failed: " + e.getMessage());
        }
    }

    @Test
    void testEncryptedFileIsDifferentFromOriginal() throws Exception {
        EncryptionUtil.encryptFile(inputFile, encryptedFile);

        byte[] originalBytes = Files.readAllBytes(inputFile);
        byte[] encryptedBytes = Files.readAllBytes(encryptedFile);

        assertFalse(java.util.Arrays.equals(originalBytes, encryptedBytes),
            "Encrypted content should not match original plaintext bytes");
    }

    // --- Utility method to reset static 'key' field in EncryptionUtil ---
    private static void resetEncryptionUtilKey(String keyString) throws Exception {
        Field keyField = EncryptionUtil.class.getDeclaredField("key");
        keyField.setAccessible(true);

        SecretKey newKey = new SecretKeySpec(keyString.getBytes(), "AES");
        keyField.set(null, newKey); // null because it's a static field
    }
}