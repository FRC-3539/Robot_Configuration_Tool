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
    private TextField projectFolderField;

    @FXML
    private TextField teamNumberField;

    @FXML
    private TextField remoteUsernameField;

    @FXML
    private PasswordField remotePasswordField;

    @FXML
    private TextField remoteDeployFolderField; // Add a field for the remote deploy folder

    private final SystemSettings systemSettings = new SystemSettings();

    @FXML
    public void initialize() {
        // Load the current settings
        projectFolderField.setText(systemSettings.getProjectFolder());
        teamNumberField.setText(systemSettings.getTeamNumber());
        remoteUsernameField.setText(systemSettings.getRemoteUsername());
        remotePasswordField.setText(systemSettings.getRemotePassword());
        remoteDeployFolderField.setText(systemSettings.getRemoteDeployFolder()); // Load the remote deploy folder
    }

    @FXML
    private void browseProjectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Folder");
        Stage stage = (Stage) projectFolderField.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            projectFolderField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void saveSettings() {
        String projectFolder = projectFolderField.getText();
        String teamNumber = teamNumberField.getText();
        String remoteUsername = remoteUsernameField.getText();
        String remotePassword = remotePasswordField.getText();
        String remoteDeployFolder = remoteDeployFolderField.getText();

        systemSettings.setProjectFolder(projectFolder);
        systemSettings.setTeamNumber(teamNumber);
        systemSettings.setRemoteUsername(remoteUsername);
        systemSettings.setRemotePassword(remotePassword);
        systemSettings.setRemoteDeployFolder(remoteDeployFolder);

        // Close the settings window
        Stage stage = (Stage) projectFolderField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelSettings() {
        // Close the settings window without saving
        Stage stage = (Stage) projectFolderField.getScene().getWindow();
        stage.close();
    }
}
