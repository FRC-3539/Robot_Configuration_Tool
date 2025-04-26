package motion.profile.mapper;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.val;

public class FXWinUtil {

    public static WinDef.HWND getNativeHandleForStage(Stage stage) {
        try {
            val getPeer = Window.class.getDeclaredMethod("getPeer", null);
            getPeer.setAccessible(true);
            val tkStage = getPeer.invoke(stage);
            val getRawHandle = tkStage.getClass().getMethod("getRawHandle");
            getRawHandle.setAccessible(true);
            val pointer = new Pointer((Long) getRawHandle.invoke(tkStage));
            return new WinDef.HWND(pointer);
        } catch (Exception ex) {
            System.err.println("Unable to determine native handle for window");
            return null;
        }
    }

    public static void setDarkMode(Stage stage, boolean darkMode) {
        val hwnd = FXWinUtil.getNativeHandleForStage(stage);
        val dwmapi = Dwmapi.INSTANCE;
        WinDef.BOOLByReference darkModeRef = new WinDef.BOOLByReference(new WinDef.BOOL(darkMode));

        dwmapi.DwmSetWindowAttribute(hwnd, 20, darkModeRef, Native.getNativeSize(WinDef.BOOLByReference.class));

        forceRedrawOfWindowTitleBar(stage);
    }

    private static void forceRedrawOfWindowTitleBar(Stage stage) {
        val maximized = stage.isMaximized();
        stage.setMaximized(!maximized);
        stage.setMaximized(maximized);
    }

}
