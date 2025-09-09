package robot.configuration.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FXINI {
    String filePath;
    SimpleStringProperty fileName = new SimpleStringProperty("");
    ObservableList<FXConstant> constants;
    Date lastModified;
    boolean temporaryFilePath = false;

    public FXINI(String filePath) {
        this(new INI(filePath));
    }

    public FXINI(String filePath, boolean temporaryFilePath) {
        this(new INI(filePath));
        this.temporaryFilePath = temporaryFilePath;
    }

    public FXINI(INI ini) {
        setFilePath(ini.filePath);
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
        String name = new File(filePath).getName();
        int dotIndex = name.lastIndexOf('.');
        this.fileName.set((dotIndex == -1) ? name : name.substring(0, dotIndex));
    }

    public void setFilePath(String absolutePath, boolean temporaryFilePath) {
        setFilePath(absolutePath);
        this.temporaryFilePath = temporaryFilePath;
    }

    public boolean isTemporaryFilePath() {
        return temporaryFilePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

}
