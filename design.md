# 🛡️ Secure File Storage System (SFSS) – Design Document

## 📌 Objective

Develop a **command-line based secure file storage system** where authenticated users can upload, download, list, and delete encrypted files stored locally. The system uses **GitHub OAuth2.0** for user authentication and AES encryption to protect data at rest.

---

## 🧱 Architecture Overview

```plaintext
+---------------------+     GitHub OAuth      +-----------------+
|  CLI Application    |<--------------------->|  GitHub Server  |
|  (Java, Spring Boot)|                       +-----------------+
|                     |
|  +----------------+ |                        +-------------------+
|  | FileManager    |------------------------->| Local File System |
|  +----------------+ |      Encrypted Files   +-------------------+
|                     |
|  +----------------+ |                        +-------------------+
|  | Authenticator  |<------------------------>| .sfss_token file  |
|  +----------------+ |     AES-encrypted      +-------------------+
|                     |
+---------------------+

Components:
- FileManager: Handles CLI-based file ops (upload, download, list, delete).
- Authenticator: Initiates GitHub OAuth, manages encrypted token.
- TokenEncryptor: Encrypts/decrypts access tokens.
- EncryptionUtil: Encrypts/decrypts files with AES.


⚙️ Core Components

1. Main.java (CLI Controller)
	Parses and executes CLI commands.
	Triggers auth if .sfss_token not present.

2. Authenticator
	Launches browser for GitHub OAuth.
	Stores encrypted token securely using TokenEncryptor.

3. TokenEncryptor
	Uses AES to encrypt and decrypt .sfss_token at rest.
	Stores symmetric key at ~/.sfss_key.

4. FileManager
	Performs encrypted upload/download.
	Validates input, limits file size (≤50 MB), sanitizes filenames.
	
5. EncryptionUtil
	Uses AES cipher for encrypting and decrypting files.


🔐 Security Considerations

-------------------------------------------------------------------------
| Feature               | Implementation Detail                         |
| --------------------- | --------------------------------------------- |
| GitHub Authentication | OAuth 2.0 flow with authorization code grant  |
| Token Storage         | AES-encrypted `.sfss_token` with 256-bit key  |
| File Encryption       | AES-256 encryption via `CipherOutputStream`   |
| Input Validation      | Filename sanitization, file size limit (50MB) |
| Rate Limiting         | Prevents excessive login attempts             |
| Logging               | Rotating log files 						  |
------------------------------------------------------------------------
