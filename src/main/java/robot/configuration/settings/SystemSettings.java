package robot.configuration.settings;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class SystemSettings {

    private static final String SETTINGS_FILE = "system_settings.properties";
    private static final String PROJECT_FOLDER_KEY = "projectFolder";
    private static final String TEAM_NUMBER_KEY = "teamNumber";
    private static final String ROBORIO_USERNAME_KEY = "roboRioUsername";
    private static final String ROBORIO_PASSWORD_KEY = "roboRioPassword";
    private static final String SECRET_KEY = "aesEncryptionKey"; // Key for AES encryption
    private static final String REMOTE_DEPLOY_FOLDER_KEY = "remoteDeployFolder"; // Add remote deploy folder key

    private final Properties properties;

    public SystemSettings() {
        properties = new Properties();
        loadSettings();
    }

    private void loadSettings() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            // Settings file does not exist, create a new one
            saveSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "System Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProjectFolder() {
        return properties.getProperty(PROJECT_FOLDER_KEY, "");
    }

    public void setProjectFolder(String folderPath) {
        properties.setProperty(PROJECT_FOLDER_KEY, folderPath);
        saveSettings();
    }

    public String getTeamNumber() {
        return properties.getProperty(TEAM_NUMBER_KEY, "");
    }

    public void setTeamNumber(String teamNumber) {
        properties.setProperty(TEAM_NUMBER_KEY, teamNumber);
        saveSettings();
    }

    public String getRemoteUsername() {
        return properties.getProperty(ROBORIO_USERNAME_KEY, "lvuser");
    }

    public void setRemoteUsername(String username) {
        properties.setProperty(ROBORIO_USERNAME_KEY, username);
        saveSettings();
    }

    public String getRemotePassword() {
        String encryptedPassword = properties.getProperty(ROBORIO_PASSWORD_KEY, "");
        return decryptPassword(encryptedPassword); // Decrypt the password before returning
    }

    public void setRemotePassword(String password) {
        String encryptedPassword = encryptPassword(password); // Encrypt the password before saving
        properties.setProperty(ROBORIO_PASSWORD_KEY, encryptedPassword);
        saveSettings();
    }

    public String getRemoteDeployFolder() {
        return properties.getProperty(REMOTE_DEPLOY_FOLDER_KEY, "/home/lvuser/deploy"); // Default to a common deploy
                                                                                        // folder
    }

    public void setRemoteDeployFolder(String folderPath) {
        properties.setProperty(REMOTE_DEPLOY_FOLDER_KEY, folderPath);
        saveSettings();
    }

    private String encryptPassword(String password) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes); // Encode to Base64 for storage
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return "";
        }
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            return new String(cipher.doFinal(decodedBytes)); // Decrypt and return the password
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private SecretKey getSecretKey() {
        // Use a fixed key for simplicity; replace with a securely generated key in
        // production
        byte[] keyBytes = SECRET_KEY.getBytes();
        return new SecretKeySpec(keyBytes, 0, 16, "AES"); // Ensure the key is 16 bytes for AES
    }
}
