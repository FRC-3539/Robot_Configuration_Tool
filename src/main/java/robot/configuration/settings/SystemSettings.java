package robot.configuration.settings;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class SystemSettings {

    private static final String SETTINGS_FILE = getSettingsFilePath();
    private static final String PROJECT_INI_KEY = "INIFolder";
    private static final String PROJECT_JAVA_FOLDER_KEY = "JavaFolder";
    private static final String TEAM_NUMBER_KEY = "teamNumber";
    private static final String ROBORIO_USERNAME_KEY = "roboRioUsername";
    private static final String ROBORIO_PASSWORD_KEY = "roboRioPassword";
    private static final String SECRET_KEY = "aesEncryptionKey"; // Key for AES encryption
    private static final String REMOTE_FOLDER_KEY = "remoteFolder"; // Add remote deploy folder key

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

    public String getINIFolder() {
        return properties.getProperty(PROJECT_INI_KEY, "");
    }

    public void setINIFolder(String folderPath) {
        properties.setProperty(PROJECT_INI_KEY, folderPath);
        saveSettings();
    }

    public String getJavaFolder() {
        return properties.getProperty(PROJECT_JAVA_FOLDER_KEY, "");
    }

    public void setJavaFolder(String folderPath) {
        properties.setProperty(PROJECT_JAVA_FOLDER_KEY, folderPath);
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

    public String getRemoteFolder() {
        return properties.getProperty(REMOTE_FOLDER_KEY, "/home/lvuser/deploy/constants"); // Default to a common
                                                                                           // deploy
        // folder
    }

    public void setRemoteFolder(String folderPath) {
        properties.setProperty(REMOTE_FOLDER_KEY, folderPath);
        saveSettings();
    }

    public String[] getRoboRioIPs() {
        String teamStr = getTeamNumber();
        if (teamStr == null || !teamStr.matches("\\d{1,5}")) {
            return new String[0];
        }
        int team = Integer.parseInt(teamStr);
        // FRC IP formats
        String ip1 = String.format("10.%d.%d.2", team / 100, team % 100);
        String ip2 = "172.22.11.2";
        String ip3 = String.format("roborio-%d-FRC.lan", team);
        String ip4 = String.format("roborio-%d-FRC.local", team);
        String ip5 = String.format("roborio-%d-FRC.frc-field.local", team);
        String ip6 = String.format("roborio-%d-FRC", team);

        return new String[] { ip1, ip2, ip3, ip4, ip5, ip6 };
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

    public static String getSettingsFolder() {
        String programData = System.getenv("ProgramData");
        if (programData == null || programData.isEmpty()) {
            programData = "C:\\ProgramData";
        }
        String appFolder = programData + File.separator + "Robot Config Tool";
        new File(appFolder).mkdirs(); // Ensure the folder exists
        return appFolder;
    }

    public static String getSettingsFilePath() {
        return getSettingsFolder() + File.separator + "robot_config_tool_settings.properties";
    }
}
