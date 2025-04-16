package motion.profile.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;

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
    private Button deleteConstantButton;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField valueField;

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
            constant.setType(event.getNewValue());
        });

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Enable editing
        constantsTableView.setEditable(true);
        nameColumn.setOnEditCommit(event -> {
            Constant constant = event.getRowValue();
            constant.setName(event.getNewValue());
        });
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

        // Add button actions
        addConstantButton.setOnAction(event -> addNewConstant());
        deleteConstantButton.setOnAction(event -> deleteSelectedConstant());
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
                    // TODO: FIX "KINDA" 
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
        String value = valueField.getText();

        // Validate name and value
        if (name != null && !name.isEmpty() && name.matches("[a-zA-Z_$][a-zA-Z\\d_$]*") && !JAVA_KEYWORDS.contains(name)) {
            if (type != null && value != null && isValidValue(value, type)) {
                constants.add(new Constant(name, type, value));
                nameField.clear();
                typeComboBox.setValue(null);
                valueField.clear();
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

    private void deleteSelectedConstant() {
        Constant selected = constantsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            constants.remove(selected);
        }
    }
}
