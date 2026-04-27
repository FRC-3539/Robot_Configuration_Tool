package robot.configuration.utils;

public enum ConstantType {
    STRING("String"),
    INT("int"),
    DOUBLE("double"),
    BOOLEAN("boolean");
    // TRANSLATION2D("Translation2d");

    private final String value;

    ConstantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ConstantType fromValue(String value) {
        for (ConstantType t : values()) {
            if (t.value.equals(value))
                return t;
        }
        return null;
    }

    public boolean equals(String other) {
        return value.equals(other);
    }
}