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
    SimpleStringProperty lastModified = new SimpleStringProperty();

    public FXINI(String filePath) {
        this(new INI(filePath));
    }

    public FXINI(INI ini) {
        setFilePath(ini.filePath);
        constants = javafx.collections.FXCollections.observableArrayList();
        for (Constant constant : ini.constants) {
            this.constants.add(new FXConstant(this, constant));
        }
        this.lastModified.set(ini.lastModified);
    }

    public ObservableList<FXConstant> getConstants() {
        return constants;
    }

    public void renameFile(String newName) {
        String directory = new File(filePath).getParent();
        if (directory == null) {
            directory = ".";
        }
        String newFilePath = directory + File.separator + newName + ".ini";
        File oldFile = new File(filePath);
        File newFile = new File(newFilePath);
        if (oldFile.renameTo(newFile)) {
            setFilePath(newFilePath);
        } else {
            System.err.println("Failed to rename file: " + filePath + " to " + newFilePath);
        }
    }

    public String getFileName() {
        String name = new File(filePath).getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? name : name.substring(0, dotIndex);
    }

    public boolean removeConstant(FXConstant constant) {
        return constants.remove(constant);
    }

    public boolean addConstant(FXConstant constant) {
        setLastModified(new Date());
        constant.setFXINI(this);
        return constants.add(constant);
    }

    public INI toINI() {
        // Convert FXConstant to Constant
        ArrayList<Constant> constantList = new ArrayList<>();
        for (FXConstant fxConstant : constants) {
            constantList.add(fxConstant.toConstant());
        }
        INI ini = new INI(this.filePath, constantList, lastModified.get());
        return ini;
    }

    public void setFilePath(String absolutePath) {
        this.filePath = absolutePath;
        String name = new File(filePath).getName();
        int dotIndex = name.lastIndexOf('.');
        this.fileName.set((dotIndex == -1) ? name : name.substring(0, dotIndex));
    }

    public String getFilePath() {
        return this.filePath;
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public SimpleStringProperty lastModifiedProperty() {
        return lastModified;
    }

    public void setLastModified(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yy hh:mm:ss a");
        this.lastModified.set(sdf.format(date));
    }

}
