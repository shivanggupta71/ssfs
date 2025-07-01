package com.shivang.ssfsOauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shivang.ssfsOauth.cli.Authenticator;
import com.shivang.ssfsOauth.cli.FileManager;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
        if (args.length < 1) {
        	logger.info("Usage: upload <file> | download <file> | list | delete <file>");
            return;
        }
        try {
        String user = Authenticator.getAuthenticatedUser();
        FileManager.init();

        switch (args[0]) {
            case "upload":
                if (args.length < 2) {
                	logger.error("Missing filename for upload.");
                    return;
                }
                FileManager.upload(args[1],user);
                break;

            case "download":
                if (args.length < 2) {
                	logger.error("Missing filename for download.");
                    return;
                }
                FileManager.download(args[1],user);
                break;

            case "list":
                FileManager.list();
                break;

            case "delete":
                if (args.length < 2) {
                	logger.error("Missing filename for deletion.");
                    return;
                }
                FileManager.delete(args[1]);
                break;

            default:
                logger.error("Unknown command. Valid commands: upload, download, list, delete.");
        }
        }catch(Exception ex) {
        	logger.error("Error occured file performing operation: "+ex.getMessage());
        }
    }

}
