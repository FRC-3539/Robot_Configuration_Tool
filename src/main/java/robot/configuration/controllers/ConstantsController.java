package robot.configuration.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import robot.configuration.settings.SystemSettings;
import robot.configuration.utils.FXConstant;
import robot.configuration.utils.FXINI;
import javafx.collections.transformation.FilteredList;

import java.io.File;
import java.io.IOException;

public class ConstantsController {

    @FXML
    private TableView<FXConstant> constantsTableView;
    @FXML
    private TableColumn<FXConstant, String> nameColumn;
    @FXML
    private TableColumn<FXConstant, String> typeColumn;
    @FXML
    private TableColumn<FXConstant, String> valueColumn;
    @FXML
    private TableColumn<FXConstant, String> descriptionColumn;
    @FXML
    private TableView<FXINI> filesTableView; // Table to list opened files
    @FXML
    private TableColumn<FXINI, String> fileNameColumn; // Column for file names
    @FXML
    private TableColumn<FXINI, String> lastModifiedColumn; // Column for last modified timestamps
    @FXML
    private Button addConstantButton;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Button uploadButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField valueField;

    @FXML
    private TextField descriptionField;

    @FXML
    private CheckBox booleanSwitch;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem newFileMenuItem;
    @FXML
    private MenuItem saveFileMenuItem;
    @FXML
    private MenuItem saveAllFilesMenuItem;

    @FXML
    private TextField searchField;

    private ObservableList<FXINI> openedFiles = FXCollections.observableArrayList();

    private static final ObservableList<String> JAVA_KEYWORDS = FXCollections.observableArrayList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true",
            "false");

    private final SystemSettings systemSettings = new SystemSettings();

    private FilteredList<FXConstant> filteredConstants = null;

    @FXML
    public void initialize() {
        // Configure constants table
        constantsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        filesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        lastModifiedColumn.setCellValueFactory(cellData -> cellData.getValue().lastModifiedProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            FXConstant constant = event.getRowValue();
            constant.setDescription(event.getNewValue());
        });

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        ObservableList<String> typeOptions = FXCollections.observableArrayList("String", "int", "double", "boolean");
        // Always show ComboBox for type column, not just on edit
        typeColumn.setCellFactory(col -> {
            return new ComboBoxTableCell<FXConstant, String>(typeOptions) {
                @Override
                public void startEdit() {
                    setStyle(
                            "-fx-background-color: -color-dark; -fx-border-color: -color-accent-emphasis; -fx-border-width: 1px; -fx-border-radius: 1px;");
                    super.startEdit();

                }

                @Override
                public void commitEdit(String newValue) {
                    setStyle(""); // Reset style to default

                    super.commitEdit(newValue);
                }

                @Override
                public void cancelEdit() {
                    setStyle(""); // Reset style to default
                    super.cancelEdit();
                }

            };
        });
        typeColumn.setOnEditCommit(event -> {
            FXConstant constant = event.getRowValue();
            String newType = event.getNewValue();
            constant.setType(newType);
            if ("boolean".equals(newType)) {
                constant.setValue("" + Boolean.parseBoolean(constant.getValue()));
            } else if ("int".equals(newType)) {
                try {
                    constant.setValue("" + Integer.parseInt(constant.getValue()));
                } catch (NumberFormatException e) {
                    constant.setValue("0");
                }
            } else if ("double".equals(newType)) {
                try {
                    constant.setValue("" + Double.parseDouble(constant.getValue()));
                } catch (NumberFormatException e) {
                    constant.setValue("0.0");
                }
            } else if ("String".equals(newType)) {
                constant.setValue(constant.getValue());
            }
            constantsTableView.refresh();
        });
        valueColumn.setCellFactory(col -> new TableCell<FXConstant, String>() {
            private final CheckBox checkBox = new CheckBox();
            private TextField textField = new TextField();
            private String originalValue;

            {
                // Toggle boolean directly in the model
                checkBox.setOnAction(e -> {
                    FXConstant row = this.getTableRow().getItem();
                    if (row != null && "boolean".equals(row.getType())) {
                        row.setValue(Boolean.toString(checkBox.isSelected()));
                    }
                });
                textField.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ESCAPE:
                            event.consume();
                            cancelEdit();
                            break;
                        case ENTER:
                            commitEdit(textField.getText());
                            break;
                        default:
                            break;
                    }
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused && wasFocused) {
                        commitEdit(textField.getText());
                    }
                });
            }

            private boolean isBooleanCell() {

                return this.getTableRow().getItem() != null && "boolean".equals(this.getTableRow().getItem().getType());
            }

            @Override
            public void startEdit() {
                if (isEmpty() || isBooleanCell()) {
                    return;
                }
                super.startEdit();

                setStyle(
                        "-fx-background-color: -color-dark; -fx-border-color: -color-accent-emphasis; -fx-border-width: 1px; -fx-border-radius: 1px;");

                originalValue = getItem();

                textField.setText(originalValue == null ? "" : originalValue);
                setGraphic(textField);
                setText(null);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                textField.requestFocus();
                textField.selectAll();

            }

            @Override
            public void cancelEdit() {
                setStyle(""); // Reset style to default
                if (!isBooleanCell()) {
                    textField.setText(originalValue);
                }
                updateItem(originalValue, false); // revert to original value
            }

            @Override
            public void commitEdit(String newValue) {
                setStyle(""); // Reset style to default
                FXConstant constant = this.getTableRow().getItem();
                String type = constant.getType();

                if (isValidValue(newValue, type)) {
                    constant.setValue(newValue);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Value");
                    alert.setHeaderText("Invalid Constant Value");
                    alert.setContentText("The value must match the selected data type.");
                    setAlertIcon(alert);
                    alert.showAndWait();
                    constantsTableView.refresh();
                }
                super.commitEdit(constant.getValue());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (isBooleanCell()) {
                    // Render checkbox
                    boolean sel = Boolean.parseBoolean(item);
                    checkBox.setSelected(sel);
                    setGraphic(checkBox);
                    setText(null);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(item);
                    setGraphic(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        });

        constantsTableView.setEditable(true);
        nameColumn.setOnEditCommit(event -> {
            FXConstant constant = event.getRowValue();
            constant.setName(event.getNewValue());
        });

        constantsTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            nameColumn.setPrefWidth(tableWidth * 0.20);
            typeColumn.setPrefWidth(tableWidth * 0.20);
            valueColumn.setPrefWidth(tableWidth * 0.20);
            descriptionColumn.setPrefWidth(tableWidth * 0.40);
        });

        filesTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            fileNameColumn.setPrefWidth(tableWidth * 0.48);
            lastModifiedColumn.setPrefWidth(tableWidth * 0.52);
        });

        typeComboBox.setItems(typeOptions);
        typeComboBox.setValue("String");

        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if ("boolean".equals(newType)) {
                valueField.setVisible(false);
                booleanSwitch.setVisible(true);
            } else {
                valueField.setVisible(true);
                booleanSwitch.setVisible(false);
            }
        });

        // Disable the "Add" button if no file is selected
        filesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldFile, newFile) -> {
            addConstantButton.setDisable(newFile == null);
        });

        // Initially disable the "Add" button
        addConstantButton.setDisable(true);

        // Add button actions
        addConstantButton.setOnAction(event -> addNewConstant());

        constantsTableView.setRowFactory(tv -> {
            TableRow<FXConstant> row = new TableRow<>();
            ContextMenu rowContextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                FXINI selectedFile = filesTableView.getSelectionModel().getSelectedItem();
                if (selectedFile == null) {
                    showError("No File Selected", "Please select a file before adding a constant.");
                    return;
                }
                FXConstant selected = row.getItem();
                if (selected != null) {
                    selectedFile.removeConstant(selected);
                }
            });
            rowContextMenu.getItems().add(deleteItem);

            // Flag to indicate if row context menu is opening
            final boolean[] rowContextMenuOpening = { false };

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    rowContextMenuOpening[0] = true;
                    rowContextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            rowContextMenu.setOnHidden(e -> rowContextMenuOpening[0] = false);

            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    rowContextMenu.hide();
                }
            });

            // Prevent fileTable context menu if row context menu is opening
            filesTableView.setOnContextMenuRequested(fileEvent -> {
                if (rowContextMenuOpening[0]) {
                    fileEvent.consume();
                }
            });

            return row;
        });

        setupMenuBar();

        // Configure files table
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        fileNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fileNameColumn.setOnEditCommit(event -> {
            FXINI file = event.getRowValue();
            String newFileName = event.getNewValue();
            if (newFileName != null && !newFileName.trim().isEmpty()) {
                file.renameFile(newFileName.trim());
                filesTableView.refresh();
            }
        });
        filesTableView.setEditable(true);
        filesTableView.setItems(openedFiles);
        filesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                loadFileConstants(newFile);
            }
        });

        ContextMenu fileTableContextMenu = new ContextMenu();
        MenuItem newItem = new MenuItem("New File");
        newItem.setOnAction(event -> {
            newFile();
        });
        fileTableContextMenu.getItems().add(newItem);
        filesTableView.setContextMenu(fileTableContextMenu);

        filesTableView.setRowFactory(tv -> {

            TableRow<FXINI> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem saveItem = new MenuItem("Save");

            saveItem.setOnAction(event -> {
                FXINI selected = row.getItem();
                if (selected != null) {
                    saveFile(selected);
                }
            });

            contextMenu.getItems().addAll(saveItem);

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                    event.consume();

                }

            });

            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            return row;
        });

        loadAllFilesFromProjectFolder();

        // Listen to search field changes and update filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateConstantsFilter(newVal);
        });

    }

    private void loadAllFilesFromProjectFolder() {
        File projectDir = new File(systemSettings.getProjectFolder());
        if (projectDir.exists() && projectDir.isDirectory()) {
            File[] iniFiles = projectDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".ini"));
            if (iniFiles != null) {
                for (File file : iniFiles) {
                    openFile(file);
                }
            }
        }
        if (!openedFiles.isEmpty())
            loadFileConstants(openedFiles.get(0));
    }

    private void setupMenuBar() {
        settingsMenuItem.setOnAction(event -> openSettingsWindow());
        newFileMenuItem.setOnAction(event -> newFile());
        saveAllFilesMenuItem.setOnAction(event -> saveAllFiles());
        saveFileMenuItem.setOnAction(event -> {
            FXINI selected = filesTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                saveFile(selected);
            }
        });
    }

    private void openSettingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Scene scene = new Scene(loader.load());
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.setScene(scene);
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to open the settings window: " + e.getMessage());
        }
    }

    private void newFile() {
        if (systemSettings.getProjectFolder() == null || systemSettings.getProjectFolder().isEmpty()) {
            showError("No Project Folder Set",
                    "Please set a valid project folder in settings before creating a new file.");
            return;
        }
        FXINI ini = new FXINI(systemSettings.getProjectFolder() + File.separator + "newFile.ini");
        ini.toINI().save();
        openedFiles.add(ini);
        filesTableView.getSelectionModel().select(ini);

    }

    private void openFile(File file) {
        FXINI ini = new FXINI(file.getAbsolutePath());
        if (openedFiles.contains(ini)) {
            return;
        }
        openedFiles.add(ini);

    }

    private boolean isValidValue(String value, String type) {
        try {
            switch (type) {
                case "int":
                    Integer.parseInt(value);
                    break;
                case "double":
                    Double.parseDouble(value);
                    break;
                case "boolean":
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Invalid Boolean value");
                    }
                    break;

                case "String":
                    break;
                default:
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void loadFileConstants(FXINI file) {
        // Use a filtered list for the selected file's constants
        filteredConstants = new FilteredList<>(file.getConstants());
        constantsTableView.setItems(filteredConstants);
        updateConstantsFilter(searchField.getText());
        filesTableView.getSelectionModel().select(file);
    }

    private void updateConstantsFilter(String filter) {
        if (filteredConstants == null)
            return;
        String lower = filter == null ? "" : filter.toLowerCase();
        filteredConstants.setPredicate(constant -> {
            if (lower.isEmpty())
                return true;
            return (constant.getName() != null && constant.getName().toLowerCase().contains(lower))
                    || (constant.getType() != null && constant.getType().toLowerCase().contains(lower))
                    || (constant.getValue() != null && constant.getValue().toLowerCase().contains(lower))
                    || (constant.getDescription() != null && constant.getDescription().toLowerCase().contains(lower));
        });
    }

    private void clearConstantsTable() {
        constantsTableView.setItems(null);
        constantsTableView.refresh();
    }

    private void addNewConstant() {
        // Ensure a file is selected before adding a constant
        FXINI selectedFile = filesTableView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            showError("No File Selected", "Please select a file before adding a constant.");
            return;
        }

        String name = nameField.getText();
        String type = typeComboBox.getValue();
        String value;
        String description = descriptionField.getText();

        if ("boolean".equals(type)) {
            value = Boolean.toString(booleanSwitch.isSelected());
        } else {
            value = valueField.getText();
        }

        if (name != null && !name.isEmpty() && name.matches("[a-zA-Z_$][a-zA-Z\\d_$]*")
                && !JAVA_KEYWORDS.contains(name)) {
            if (type != null && value != null && isValidValue(value, type)) {
                selectedFile.addConstant(new FXConstant(selectedFile, name, type, value, description));
                nameField.clear();
                valueField.clear();
                descriptionField.clear();
                booleanSwitch.setSelected(false);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Value");
                alert.setHeaderText("Invalid Constant Value");
                alert.setContentText("The value must match the selected data type.");
                setAlertIcon(alert);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Name");
            alert.setHeaderText("Invalid Constant Name");
            alert.setContentText("The name must follow Java variable naming conventions and cannot be a Java keyword.");
            setAlertIcon(alert);
            alert.showAndWait();
        }
    }

    private void saveFile(FXINI iniFile) {
        if (iniFile == null) {
            showError("No File Selected", "Please select a file to save.");
            return;
        }
        iniFile.toINI().save(); // This will save the file at the new location

    }

    private void saveAllFiles() {

        for (FXINI fxini : openedFiles) {
            saveFile(fxini);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        setAlertIcon(alert);
        alert.showAndWait();
    }

    // Utility method to set the icon for any alert
    private void setAlertIcon(Alert alert) {
        // Force the alert's stage to be created
        alert.getDialogPane().applyCss();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        java.io.InputStream iconStream = getClass().getResourceAsStream("/icon.png");
        if (iconStream != null) {
            stage.getIcons().clear();
            stage.getIcons().add(new javafx.scene.image.Image(iconStream));
        }
    }
}
