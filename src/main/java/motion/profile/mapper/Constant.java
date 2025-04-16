package motion.profile.mapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Constant {

    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty value;

    public Constant(String name, String type, String value) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleStringProperty(value);
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
}
