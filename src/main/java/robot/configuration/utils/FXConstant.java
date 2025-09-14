package robot.configuration.utils;

import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FXConstant {

    private final StringProperty name;
    private final StringProperty type;
    private final StringProperty value;
    private final StringProperty description;
    FXINI ini;

    public FXConstant(FXINI ini, String name, String type, String value, String description) {
        this.ini = ini;
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleStringProperty(value != null ? value.toString() : "");
        this.description = new SimpleStringProperty(description);
    }

    public FXConstant(FXINI ini, Constant constant) {
        this(ini, constant.getName(), constant.getType(), constant.getValue(), constant.getDescription());
    }

    public void setFXINI(FXINI ini) {
        this.ini = ini;
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
        ini.setLastModified(new Date());
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
        ini.setLastModified(new Date());

    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
        ini.setLastModified(new Date());

    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
        ini.setLastModified(new Date());

    }
}
