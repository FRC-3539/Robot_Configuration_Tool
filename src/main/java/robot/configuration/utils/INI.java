package robot.configuration.utils;

import java.util.List;

import org.ini4j.Wini;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class INI {
    Path filePath;
    List<Constant> constants;
    String lastModified;

    public INI(Path filePath,
            List<Constant> constants,
            String lastModified) {
        this.filePath = filePath;
        this.constants = constants;
        this.lastModified = lastModified;
    }

    public INI(Path filePath) {
        this.filePath = filePath;
        constants = new ArrayList<>();

        try {
            if (!Files.exists(filePath)) {
                System.err.println("INI configuration Load: " + filePath + " does not exist.");
                return;
            }

            String fileName = filePath.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = fileName.substring(0, dotIndex);
            }

            Wini ini = new Wini(filePath.toFile());

            // Use the file name (without extension) as the values section
            org.ini4j.Profile.Section valuesSection = ini.get(fileName);
            org.ini4j.Profile.Section typeSection = ini.get("Type");
            org.ini4j.Profile.Section commentSection = ini.get("Comment");
            org.ini4j.Profile.Section lastModifiedSection = ini.get("lastModified");

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
            if (lastModifiedSection != null) {
                lastModified = lastModifiedSection.get("timestamp");
            } else {
                lastModified = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean save() throws IOException {
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        Wini wini = new Wini(filePath.toFile());

        String sectionName = filePath.getFileName().toString();
        int dotIndex = sectionName.lastIndexOf('.');
        if (dotIndex > 0) {
            sectionName = sectionName.substring(0, dotIndex);
        }

        // Clear existing sections
        wini.remove(sectionName);
        wini.remove("Type");
        wini.remove("Comment");
        wini.remove("lastModified");

        // Add sections
        org.ini4j.Profile.Section valueSection = wini.add(sectionName);
        org.ini4j.Profile.Section typeSection = wini.add("Type");
        org.ini4j.Profile.Section commentSection = wini.add("Comment");
        org.ini4j.Profile.Section lastModifiedSection = wini.add("lastModified");

        for (Constant constant : constants) {
            valueSection.put(constant.getName(), constant.getValue() != null ? constant.getValue().toString() : "");
            typeSection.put(constant.getName(),
                    constant.getType() != null ? constant.getType() : String.class.getName());
            commentSection.put(constant.getName(),
                    constant.getDescription() != null ? constant.getDescription() : "");
        }

        lastModifiedSection.put("timestamp", lastModified);

        wini.store();
        return true;
    }
}
