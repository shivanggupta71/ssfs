# Secure File Storage System (SFSS)

## Overview

SFSS is a **command-line based secure file storage application** built with Java and Spring Boot. It allows users to **upload, download, list, and delete files** stored locally on their machine with the following key features:

- **GitHub OAuth 2.0 Authentication**: Ensures only authenticated users can access file operations.
- **AES Encryption**: All files are encrypted before being stored locally.
- **Input Validation & Security**: Prevents path traversal and secures sensitive tokens.
- **Logging**: Basic logs for authentication and file operations.

---

## Architecture & Design

- **Spring Boot Backend**: Handles OAuth login flow with GitHub, token management, and authentication.
- **CLI Interface**: Java-based command-line client that interacts with the backend for auth and performs secure file operations.
- **Storage**: Files encrypted with AES and saved under `~/sfss-storage/`.
- **Authentication Tokens**: Stored securely in `~/.sfss_token`.

---

## Prerequisites

- Java 17 or later
- Maven 3.x
- GitHub OAuth App registered with:
  - Authorization callback URL: `http://localhost:8000/login/oauth2/code/github`
- GitHub OAuth Client ID and Client Secret
- create a file .key_string inside user.home and insert a 32 byte string used for AES encryption of file.
    
    echo "32 character string" >.key_string

---

## Setup

1. **Clone the repository**

   bash
   git clone <your-repo-url>
   cd sfss-cli

2. **Configure OAuth credentials**

Edit src/main/resources/application.properties and set:

      github.client.id=YOUR_CLIENT_ID
      github.client.secret=YOUR_CLIENT_SECRET
      github.redirect.uri=http://localhost:8000/login/oauth2/code/github

3. **Build the project**

      mvn clean package or mvn clean install


## Running the Application

   Start the OAuth Server. This server handles GitHub login and token storage:

         java -jar target/sfss-cli-1.0.0.jar

### Using the CLI Client

Open a new terminal and run commands via Maven or provided scripts.

Using Maven:

    mvn exec:java -Dexec.mainClass="com.example.sfss.Main" -Dexec.args="upload /path/to/file.txt"
    mvn exec:java -Dexec.mainClass="com.example.sfss.Main" -Dexec.args="list"
    mvn exec:java -Dexec.mainClass="com.example.sfss.Main" -Dexec.args="download file.txt"
    mvn exec:java -Dexec.mainClass="com.example.sfss.Main" -Dexec.args="delete file.txt"

### Using helper scripts:

  Linux/macOS

    ./sfss-cli.sh upload /path/to/file.txt
    ./sfss-cli.sh list
    ./sfss-cli.sh download filename.txt
    ./sfss-cli.sh delete filename.txt

  Windows
  
    sfss-cli.bat upload C:\path\to\file.txt
    sfss-cli.bat list


✅ IMPLEMENTED

1.Core Functionality

    a. Upload, Download, List, Delete via CLI (Main.java with FileManager).
    b. GitHub OAuth for authentication (CLI → Spring Boot backend → GitHub).
    c. Authenticated-only access enforced via .sfss_token.

2.🔐 Security Considerations

    a. AES Encryption for files using EncryptionUtil.
    b. Filename validation and sanitization to prevent path traversal.
    c. Token encryption at rest using TokenEncryptor and .sfss_key.
    d. Encrypted token storage ensures sensitive data isn't stored in plaintext.
    e. File size restriction upto 50 MB allowed.
    f. Input sanitization & proper error handling.


3.Software Engineering Best Practices:

    a. Design documentation in DESIGN.md with architecture diagram.
    b. GitHub version control, organized project, meaningful commits.
    c. README.md with setup, usage, and security notes.
    d. Unit Tests:
        EncryptionUtilTest for file encryption.
        TokenEncryptorTest for token encryption/decryption.
        Tests likely exist for FileManager, Authenticator, and rate limiter.
    e. Logging:
        Rotating file logs (daily, 7-day retention).


4.Simplifications Handled

    TLS omitted (acceptable due to CLI/local scope).
    Docker not included (as scoped).


📌 Assumptions

    Files are stored on a local file system.
    GitHub OAuth token scope is limited to read:user.
    Users are trusted with symmetric key stored locally.

### Note: On first CLI command run, your default browser will open for GitHub OAuth authentication.

