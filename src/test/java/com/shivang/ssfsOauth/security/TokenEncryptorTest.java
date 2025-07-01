package com.shivang.ssfsOauth.security;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
public class TokenEncryptorTest {

	private static final String TEST_TOKEN = "gho_exampleaccesstoken123456789";
    private static Path tempTokenFile;

    @BeforeAll
    static void setup() throws IOException {
        // Create a temporary file to simulate encrypted token storage
        tempTokenFile = Files.createTempFile("sfss_test_token", ".enc");
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempTokenFile);
    }

    @Test
    void testEncryptAndDecryptToken() {
        try {
            // Encrypt token to file
            TokenEncryptor.encryptToFile(TEST_TOKEN, tempTokenFile.toString());

            // Decrypt token from file
            String decryptedToken = TokenEncryptor.decryptFromFile(tempTokenFile.toString());

            assertEquals(TEST_TOKEN, decryptedToken, "Decrypted token should match the original token");
        } catch (Exception e) {
            fail("Encryption or decryption threw an exception: " + e.getMessage());
        }
    }

    @Test
    void testKeyFileExistsAfterEncryption() throws Exception {
        TokenEncryptor.encryptToFile(TEST_TOKEN, tempTokenFile.toString());
        Path keyPath = Paths.get(System.getProperty("user.home"), ".sfss_key");

        assertTrue(Files.exists(keyPath), "AES key file should exist after encryption");
        assertTrue(Files.size(keyPath) >= 32, "AES key file should contain a valid key (256 bits)");
    }
}
