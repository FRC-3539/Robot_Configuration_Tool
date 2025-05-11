package robot.configuration.systems;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FXWinUtil {

    public static WinDef.HWND getNativeHandleForStage(Stage stage) {
        try {
            java.lang.reflect.Method getPeer = Window.class.getDeclaredMethod("getPeer");
            getPeer.setAccessible(true);
            Object tkStage = getPeer.invoke(stage);
            java.lang.reflect.Method getRawHandle = tkStage.getClass().getMethod("getRawHandle");
            getRawHandle.setAccessible(true);
            Pointer pointer = new Pointer((Long) getRawHandle.invoke(tkStage));
            return new WinDef.HWND(pointer);
        } catch (Exception ex) {
            System.err.println("Unable to determine native handle for window");
            ex.printStackTrace();
            return null;
        }
    }

    public static void setDarkMode(Stage stage, boolean darkMode) {
        WinDef.HWND hwnd = getNativeHandleForStage(stage);
        if (hwnd == null) {
            System.err.println("Failed to get HWND for the stage.");
            return;
        }

        Dwmapi dwmapi = Dwmapi.INSTANCE;
        WinDef.BOOLByReference darkModeRef = new WinDef.BOOLByReference(new WinDef.BOOL(darkMode));

        int result = dwmapi.DwmSetWindowAttribute(hwnd, 20, darkModeRef,
                Native.getNativeSize(WinDef.BOOLByReference.class));
        if (result != 0) {
            System.err.println("Failed to set dark mode. Error code: " + result);
        }
    }
}
