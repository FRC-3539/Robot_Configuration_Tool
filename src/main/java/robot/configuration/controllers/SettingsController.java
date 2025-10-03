package robot.configuration.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import java.io.File;
import robot.configuration.settings.SystemSettings;

public class SettingsController {

    @FXML
    private TextField iniFolderField;

    @FXML
    private TextField javaFolderField;

    @FXML
    private TextField teamNumberField;

    @FXML
    private TextField remoteUsernameField;

    @FXML
    private PasswordField remotePasswordField;

    @FXML
    private TextField remoteFolderField; // Add a field for the remote deploy folder

    private final SystemSettings systemSettings = SystemSettings.getSettings();

    @FXML
    public void initialize() {
        // Load the current settings
        iniFolderField.setText(systemSettings.getINIFolder());
        teamNumberField.setText(systemSettings.getTeamNumber());
        remoteUsernameField.setText(systemSettings.getRemoteUsername());
        remotePasswordField.setText(systemSettings.getRemotePassword());
        remoteFolderField.setText(systemSettings.getRemoteFolder()); // Load the remote deploy folder
        javaFolderField.setText(systemSettings.getJavaFolder());
    }

    @FXML
    private void browseINIFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select INI Folder");
        Stage stage = (Stage) iniFolderField.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            iniFolderField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void browseJavaFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Java Folder");
        Stage stage = (Stage) javaFolderField.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            javaFolderField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void saveSettings() {
        boolean reloadRequired = false;
        String iniFolder = iniFolderField.getText();
        String teamNumber = teamNumberField.getText();
        String remoteUsername = remoteUsernameField.getText();
        String remotePassword = remotePasswordField.getText();
        String remoteFolder = remoteFolderField.getText();
        String javaFolder = javaFolderField.getText();

        if (!iniFolder.equals(systemSettings.getINIFolder())) {

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved configuration changes will be lost");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Unsaved changes in the configuration files will be lost. Would you like to continue?");

            javafx.scene.control.ButtonType yesButton = new javafx.scene.control.ButtonType("Continue");
            javafx.scene.control.ButtonType noButton = new javafx.scene.control.ButtonType("Cancel",
                    javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(yesButton, noButton);

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == noButton) {
                return;
            } else {
                reloadRequired = true;
            }
        }

        // Validate team number: must be a number and <= 5 digits
        if (!teamNumber.matches("\\d{1,5}")) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Invalid Team Number");
            alert.setHeaderText("Team Number Error");
            alert.setContentText("Team number must be a number with at most 5 digits.");
            alert.showAndWait();
            return;
        }

        systemSettings.setINIFolder(iniFolder);
        systemSettings.setTeamNumber(teamNumber);
        systemSettings.setRemoteUsername(remoteUsername);
        systemSettings.setRemotePassword(remotePassword);
        systemSettings.setRemoteFolder(remoteFolder);
        systemSettings.setJavaFolder(javaFolder);

        // Close the settings window
        Stage stage = (Stage) iniFolderField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelSettings() {
        // Close the settings window without saving
        Stage stage = (Stage) iniFolderField.getScene().getWindow();
        stage.close();
    }
}
