package robot.configuration.utils;

public class Constant {

    private String name;
    private String value;
    private String description;
    private String type;

    public Constant(String name, String type, String value, String description) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
