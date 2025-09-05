package robot.configuration.utils;

import java.util.List;
import org.ini4j.Wini;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class INI {
    String filePath;
    List<Constant> constants;
    Date lastModified;

    public INI(String filePath,
            List<Constant> constants,
            Date lastModified) {
        this.filePath = filePath;
        this.constants = constants;
        this.lastModified = lastModified;
    }

    public INI(String filePath) {
        this.filePath = filePath;
        constants = new ArrayList<>();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("INI configuration Load: " + filePath + " does not exist.");
                return;
            }

            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);
            }

            Wini ini = new Wini(file);

            // Use the file name (without extension) as the values section
            org.ini4j.Profile.Section valuesSection = ini.get(fileName);
            org.ini4j.Profile.Section typeSection = ini.get("Type");
            org.ini4j.Profile.Section commentSection = ini.get("Comment");

            if (valuesSection != null && typeSection != null && commentSection != null) {
                for (String key : valuesSection.keySet()) {
                    String type;
                    String comment;
                    try {
                        type = typeSection.get(key);
                        if (type == null)
                            type = String.class.getName();
                    } catch (Exception e) {
                        type = String.class.getName();
                    }
                    try {
                        comment = commentSection.get(key);
                        if (comment == null)
                            comment = "";
                    } catch (Exception e) {
                        comment = "";
                    }
                    String value;
                    try {
                        value = valuesSection.get(key);
                    } catch (Exception e) {
                        value = ""; // Default to empty string if parsing fails
                    }

                    constants.add(new Constant(key, type, value, comment));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean save() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            Wini wini = new Wini(new File(filePath));
            String sectionName = new File(filePath).getName();
            int dotIndex = sectionName.lastIndexOf('.');
            if (dotIndex > 0) {
                sectionName = sectionName.substring(0, dotIndex);
            }

            // Clear existing sections
            wini.remove(sectionName);
            wini.remove("Type");
            wini.remove("Comment");

            // Add sections
            org.ini4j.Profile.Section valueSection = wini.add(sectionName);
            org.ini4j.Profile.Section typeSection = wini.add("Type");
            org.ini4j.Profile.Section commentSection = wini.add("Comment");

            for (Constant constant : constants) {
                valueSection.put(constant.getName(), constant.getValue() != null ? constant.getValue().toString() : "");
                typeSection.put(constant.getName(),
                        constant.getType() != null ? constant.getType() : String.class.getName());
                commentSection.put(constant.getName(),
                        constant.getDescription() != null ? constant.getDescription() : "");
            }

            wini.store();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
