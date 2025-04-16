package motion.profile.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ConstantsController {

    @FXML
    private TableView<Constant> constantsTableView;
    @FXML
    private TableColumn<Constant, String> nameColumn;
    @FXML
    private TableColumn<Constant, String> typeColumn;
    @FXML
    private TableColumn<Constant, String> valueColumn;
    @FXML
    private Button addConstantButton;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField valueField;

    @FXML
    private CheckBox booleanSwitch; // Add a CheckBox for Boolean input

    private ObservableList<Constant> constants = FXCollections.observableArrayList();

    private static final ObservableList<String> JAVA_KEYWORDS = FXCollections.observableArrayList(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
        "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
        "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
        "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
        "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false"
    );

    @FXML
    public void initialize() {
        // Set column resize policy programmatically
        constantsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Configure table columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        // Set predefined options for the type column
        ObservableList<String> typeOptions = FXCollections.observableArrayList("String", "Integer", "Double", "Boolean");
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(typeOptions));
        typeColumn.setOnEditCommit(event -> {
            Constant constant = event.getRowValue();
            String newType = event.getNewValue();

            // Update the type and reset the value if necessary
            constant.setType(newType);
            if ("Boolean".equals(newType)) {
                constant.setValue("false"); // Default value for Boolean
            } else {
                constant.setValue(""); // Clear value for other types
            }

            // Refresh the table to reflect the changes
            constantsTableView.refresh();
        });

        // Set a custom cell factory for the valueColumn to handle Boolean values
        valueColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Constant constant = getTableView().getItems().get(getIndex());
                    if ("Boolean".equals(constant.getType())) {
                        constant.setValue(Boolean.toString(checkBox.isSelected()));
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
                    Constant constant = getTableView().getItems().get(getIndex());
                    if ("Boolean".equals(constant.getType())) {
                        checkBox.setSelected(Boolean.parseBoolean(item));
                        setGraphic(checkBox);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(item);
                    }
                }
            }
        });

        // Update valueColumn's OnEditCommit to handle dynamic type changes
        valueColumn.setOnEditCommit(event -> {
            Constant constant = event.getRowValue();
            String newValue = event.getNewValue();
            String type = constant.getType();

            if (isValidValue(newValue, type)) {
                constant.setValue(newValue);
            } else {
                // Show an alert if the value is invalid
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Value");
                alert.setHeaderText("Invalid Constant Value");
                alert.setContentText("The value must match the selected data type.");
                alert.showAndWait();
                constantsTableView.refresh(); // Revert to the old value
            }
        });

        // Enable editing
        constantsTableView.setEditable(true);
        nameColumn.setOnEditCommit(event -> {
            Constant constant = event.getRowValue();
            constant.setName(event.getNewValue());
        });

        constantsTableView.setItems(constants);

        // Bind column widths to the table width
        constantsTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            nameColumn.setPrefWidth(tableWidth * 0.25);
            typeColumn.setPrefWidth(tableWidth * 0.25);
            valueColumn.setPrefWidth(tableWidth * 0.50);
        });

        // Populate typeComboBox with predefined options
        typeComboBox.setItems(typeOptions);

        // Add a listener to typeComboBox to toggle between TextField and CheckBox
        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if ("Boolean".equals(newType)) {
                valueField.setVisible(false);
                booleanSwitch.setVisible(true);
            } else {
                valueField.setVisible(true);
                booleanSwitch.setVisible(false);
            }
        });

        // Add button actions
        addConstantButton.setOnAction(event -> addNewConstant());

        // Add context menu to table rows
        constantsTableView.setRowFactory(tv -> {
            TableRow<Constant> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                Constant selected = row.getItem();
                if (selected != null) {
                    constants.remove(selected);
                }
            });

            contextMenu.getItems().add(deleteItem);

            // Show context menu only for non-empty rows
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            // Hide context menu when clicking elsewhere
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

    private boolean isValidValue(String value, String type) {
        try {
            switch (type) {
                case "Integer":
                    Integer.parseInt(value);
                    break;
                case "Double":
                    Double.parseDouble(value);
                    break;
                case "Boolean":
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Invalid Boolean value");
                    }
                    break;
                    
                case "String":
                    // Strings are always valid KINDA
                    // TODO: FIX "KINDA" like escape sequences...
                    break;
                default:
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void addNewConstant() {
        String name = nameField.getText();
        String type = typeComboBox.getValue();
        String value;

        // Get value from the appropriate input field
        if ("Boolean".equals(type)) {
            value = Boolean.toString(booleanSwitch.isSelected());
        } else {
            value = valueField.getText();
        }

        // Validate name and value
        if (name != null && !name.isEmpty() && name.matches("[a-zA-Z_$][a-zA-Z\\d_$]*") && !JAVA_KEYWORDS.contains(name)) {
            if (type != null && value != null && isValidValue(value, type)) {
                constants.add(new Constant(name, type, value));
                nameField.clear();
                valueField.clear();
                booleanSwitch.setSelected(false);
            } else {
                // Show an alert if the value is invalid
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Value");
                alert.setHeaderText("Invalid Constant Value");
                alert.setContentText("The value must match the selected data type.");
                alert.showAndWait();
            }
        } else {
            // Show an alert if the name is invalid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Name");
            alert.setHeaderText("Invalid Constant Name");
            alert.setContentText("The name must follow Java variable naming conventions and cannot be a Java keyword.");
            alert.showAndWait();
        }
    }
}
