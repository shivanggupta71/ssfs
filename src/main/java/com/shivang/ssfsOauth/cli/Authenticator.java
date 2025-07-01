package com.shivang.ssfsOauth.cli;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.shivang.ssfsOauth.security.TokenEncryptor;

public class Authenticator {
	
	private static final Logger logger = LoggerFactory.getLogger(Authenticator.class);
	private static final String TOKEN_FILE = System.getProperty("user.home") + "/.sfss_token";
	

	public static String getAuthenticatedUser() throws Exception {
		
		logger.info("Initiating Authentication....");
        Path tokenPath = Paths.get(System.getProperty("user.home"), ".sfss_token");
        
      //checking if valid token present or not. If not, then ini tiate OAuth2.0 auth
        if (!Files.exists(Paths.get(TOKEN_FILE)) || getAccessToken() == null) { 
            logger.info("Authentication required. Launching browser...");
            Desktop.getDesktop().browse(new URI("http://localhost:8000/login"));

            while (!Files.exists(tokenPath)) {
                Thread.sleep(1000);
            }
        }

        String[] parts = getAccessToken().split(":"); //fetching username & access token from encrypted token
        return parts[0]; // returning user name
    }
	
	public static String getAccessToken() throws Exception {
        return TokenEncryptor.decryptFromFile(TOKEN_FILE);
    }
}
