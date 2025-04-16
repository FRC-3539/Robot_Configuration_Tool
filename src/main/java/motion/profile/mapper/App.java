package motion.profile.mapper;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static Stage primaryStage;
    public static ConstantsController cController;

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/constants.fxml"));
        Scene scene2 = new Scene(loader2.load());
        cController = loader2.getController();
        stage.setTitle("Constants");
        stage.setScene(scene2);

        // TODO: see if this is necessary once we build

        // Temporarily set the stage to always be on top
        stage.setAlwaysOnTop(true);
        stage.show();

        // Bring the stage to the front after it has been shown and then set always on
        // top to false
        Platform.runLater(() -> {
            stage.toFront();
            stage.setAlwaysOnTop(false);
        });

    }

    public static void main(String[] args) {
        launch();
    }
}
