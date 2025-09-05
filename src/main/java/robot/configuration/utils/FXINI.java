package robot.configuration.utils;

import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FXINI {
    String filePath;
    ObservableList<FXConstant> constants;
    Date lastModified;

    public FXINI(String filePath) {
        this(new INI(filePath));
    }

    public FXINI(INI ini) {
        this.filePath = ini.filePath;
        constants = javafx.collections.FXCollections.observableArrayList();
        for (Constant constant : ini.constants) {
            this.constants.add(new FXConstant(constant));
        }
    }

    public ObservableList<FXConstant> getConstants() {
        return constants;
    }

    public String getFileName() {
        String name = new File(filePath).getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? name : name.substring(0, dotIndex);
    }

    public boolean removeConstant(FXConstant constant) {
        return constants.remove(constant);
    }

    public INI toINI() {
        // Convert FXConstant to Constant
        ArrayList<Constant> constantList = new ArrayList<>();
        for (FXConstant fxConstant : constants) {
            constantList.add(fxConstant.toConstant());
        }
        INI ini = new INI(this.filePath, constantList, new Date());
        return ini;
    }

    public void setFilePath(String absolutePath) {
        this.filePath = absolutePath;
    }

}
