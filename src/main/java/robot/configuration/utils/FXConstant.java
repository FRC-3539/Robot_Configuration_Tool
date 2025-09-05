package robot.configuration.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXConstant {

    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty value;
    private final StringProperty description;

    public FXConstant(String name, String type, String value, String description) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleStringProperty(value != null ? value.toString() : "");
        this.description = new SimpleStringProperty(description);
    }

    public FXConstant(Constant constant) {
        this(constant.getName(), constant.getType(), constant.getValue(), constant.getDescription());
    }

    public Constant toConstant() {
        return new Constant(
                name.get(),
                type.get(),
                value.get(),
                description.get());
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
