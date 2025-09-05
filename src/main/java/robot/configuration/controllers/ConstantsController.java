package robot.configuration.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import robot.configuration.settings.SystemSettings;
import robot.configuration.utils.FXConstant;
import robot.configuration.utils.FXINI;

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
    private Button addConstantButton;
    @FXML
    private ComboBox<String> typeComboBox;

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

    private ObservableList<FXINI> openedFiles = FXCollections.observableArrayList();

    private static final ObservableList<String> JAVA_KEYWORDS = FXCollections.observableArrayList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true",
            "false");

    private final SystemSettings systemSettings = new SystemSettings();

    @FXML
    public void initialize() {
        // Configure constants table
        constantsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
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
            return new TableCell<FXConstant, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>(typeOptions);
                {
                    comboBox.setMaxWidth(Double.MAX_VALUE);
                    comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        if (getTableRow() != null && getTableRow().getItem() != null && newVal != null) {
                            FXConstant constant = (FXConstant) getTableRow().getItem();
                            if (!newVal.equals(constant.getType())) {
                                constant.setType(newVal);
                                // Optionally trigger value conversion here if needed
                                getTableView().refresh();
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                        setText(null);
                    }
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
        valueColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final TextField textField = new TextField();

            {
                checkBox.setOnAction(event -> {
                    FXConstant constant = getTableView().getItems().get(getIndex());
                    if ("boolean".equals(constant.getType())) {
                        constant.setValue(Boolean.toString(checkBox.isSelected()));
                        getTableView().refresh();
                    }
                });

                textField.setOnAction(event -> {
                    FXConstant constant = getTableView().getItems().get(getIndex());
                    if (!"boolean".equals(constant.getType())) {
                        constant.setValue(textField.getText());
                        getTableView().refresh();
                    }
                });

                // Make the text field look editable
                textField.setEditable(true);
                // textField.setStyle(
                // "-fx-background-color: -fx-control-inner-background; -fx-border-color:
                // -fx-box-border; -fx-border-width: 1;");
                // textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                // if (newVal) {
                // textField.setStyle(
                // "-fx-background-color: #e6f7ff; -fx-border-color: #1890ff;
                // -fx-border-width:2;");
                // } else {
                // textField.setStyle(
                // "-fx-background-color: -fx-control-inner-background; -fx-border-color:
                // -fx-box-border; -fx-border-width: 1;");
                // }
                // });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    FXConstant constant = getTableView().getItems().get(getIndex());
                    if ("boolean".equals(constant.getType())) {
                        checkBox.setSelected(Boolean.parseBoolean(item));
                        setGraphic(checkBox);
                        setText(null);
                    } else {
                        textField.setText(item);
                        setGraphic(textField);
                        setText(null);
                    }
                }
            }
        });

        valueColumn.setOnEditCommit(event -> {
            FXConstant constant = event.getRowValue();
            String newValue = event.getNewValue();
            String type = constant.getType();

            if (isValidValue(newValue, type)) {
                constant.setValue(newValue);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Value");
                alert.setHeaderText("Invalid Constant Value");
                alert.setContentText("The value must match the selected data type.");
                alert.showAndWait();
                constantsTableView.refresh();
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
            valueColumn.setPrefWidth(tableWidth * 0.30);
            descriptionColumn.setPrefWidth(tableWidth * 0.30);
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
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                // Ensure a file is selected before adding a constant
                FXINI selectedFile = filesTableView.getSelectionModel().getSelectedItem();
                if (selectedFile == null) {
                    showError("No File Selected", "Please select a file before adding a constant.");
                    return;
                }

                FXConstant selected = row.getItem();
                if (selected != null) {
                    if (selectedFile != null) {
                        selectedFile.removeConstant(selected);
                    }
                }
            });

            contextMenu.getItems().add(deleteItem);

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            return row;
        });

        setupMenuBar();

        // Configure files table
        fileNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        filesTableView.setItems(openedFiles);
        filesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                loadFileConstants(newFile);
            }
        });
    }

    private void setupMenuBar() {
        Menu fileMenu = new Menu("File");

        MenuItem settingsMenuItem = new MenuItem("Settings");
        settingsMenuItem.setOnAction(event -> openSettingsWindow());

        MenuItem openFileMenuItem = new MenuItem("Open File");
        openFileMenuItem.setOnAction(event -> openFile());

        MenuItem newFileMenuItem = new MenuItem("New File");
        newFileMenuItem.setOnAction(event -> newFile());

        MenuItem saveFileMenuItem = new MenuItem("Save");
        saveFileMenuItem.setOnAction(event -> saveSelectedFile());

        fileMenu.getItems().addAll(settingsMenuItem, openFileMenuItem, newFileMenuItem, saveFileMenuItem);
        menuBar.getMenus().add(fileMenu);
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
        FXINI ini = new FXINI("newFile.ini");
        openedFiles.add(ini);
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(systemSettings.getProjectFolder()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.ini"));
        File selectedFile = fileChooser.showOpenDialog(filesTableView.getScene().getWindow());

        if (selectedFile != null) {
            openFile(selectedFile);
        }
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
        constantsTableView.setItems(file.getConstants());
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
                selectedFile.getConstants().add(new FXConstant(name, type, value, description));
                nameField.clear();
                valueField.clear();
                descriptionField.clear();
                booleanSwitch.setSelected(false);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Value");
                alert.setHeaderText("Invalid Constant Value");
                alert.setContentText("The value must match the selected data type.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Name");
            alert.setHeaderText("Invalid Constant Name");
            alert.setContentText("The name must follow Java variable naming conventions and cannot be a Java keyword.");
            alert.showAndWait();
        }
    }

    private void saveSelectedFile() {
        FXINI selectedFile = filesTableView.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            showError("No File Selected", "Please select a file to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialDirectory(new File(systemSettings.getProjectFolder()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files", "*.ini"));
        fileChooser.setInitialFileName(selectedFile.getFileName() + ".ini");
        File file = fileChooser.showSaveDialog(filesTableView.getScene().getWindow());

        if (file != null) {
            selectedFile.setFilePath(file.getAbsolutePath());
            selectedFile.toINI().save(); // This will save the file at the new location
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
