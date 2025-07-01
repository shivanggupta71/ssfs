package com.shivang.ssfsOauth.cli;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shivang.ssfsOauth.security.EncryptionUtil;


public class FileManager {

	private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
	private static final Path storageDir = Paths.get(System.getProperty("user.home"), "sfss-storage");

	// Allow only alphanumeric characters, dots, dashes and underscores in filenames
	private static final String SAFE_FILENAME_REGEX = "^[a-zA-Z0-9._-]+$";
	private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 50 MB

	public static void init() throws IOException {
		Files.createDirectories(storageDir);
	}

	public static void upload(String path, String user) throws Exception {
		logger.info("File upload started...");

		Path source = Paths.get(path);
		if (source == null || !source.toFile().exists()) {
			throw new IllegalArgumentException("File does not exist.");
		} else if (source.getFileName() != null) {
			validateFileName(source.getFileName().toString());
		}
		long fileSize = Files.size(source);
		if (fileSize > MAX_FILE_SIZE_BYTES) {
			throw new IllegalArgumentException("File size exceeds 50 MB limit.");
		}
		Path target = storageDir.resolve(source.getFileName().toString());
		EncryptionUtil.encryptFile(source, target);
		logger.info("File uploaded successfully: " + source.getFileName().toString());
	}

	public static void download(String filename, String user) throws Exception {

		if (filename != null) {
			validateFileName(filename);
		}
		Path encrypted = storageDir.resolve(filename);
		Path decrypted = Paths.get(filename);
		EncryptionUtil.decryptFile(encrypted, decrypted);
		logger.info("File download completed: " + filename);
	}

	public static void list() throws IOException {
		logger.info("List of documents: ");
		Files.list(storageDir).forEach(f -> System.out.println(f.getFileName()));
	}

	public static void delete(String filename) throws IOException {
		if (filename != null) {
			validateFileName(filename);
		}
		Files.deleteIfExists(storageDir.resolve(filename));
		logger.info("Deleted: " + filename);
	}

	/*
	 * Safe file name handling. This will prevent path traversal attack.
	 */
	public static void validateFileName(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException("File name is empty or null.");
		}

		if (!fileName.matches(SAFE_FILENAME_REGEX)) {
			throw new IllegalArgumentException(
					"Invalid file name. Use only letters, numbers, dots, dashes, and underscores.");
		}

		if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
			throw new IllegalArgumentException("Illegal file path detected.");
		}
	}
}
