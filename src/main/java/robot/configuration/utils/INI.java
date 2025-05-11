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

    public INI(String filePath) {
        this.filePath = filePath;

        try {
            Wini ini = new Wini(new File(filePath));
            constants = new ArrayList<>();
            for (String sectionName : ini.keySet()) {
                Constant constant = new Constant(sectionName, ini.get(sectionName, "type"),
                        ini.get(sectionName, "value"), ini.get(sectionName, "description"));
                constants.add(constant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
