package motion.profile.mapper;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class App extends Application {

    public static Stage primaryStage;
    public static ConstantsController cController;

    @Override
    public void start(Stage stage) {
        try {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

            // Load the main content
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/constants.fxml"));
            Region mainContent = loader2.load();
            cController = loader2.getController();

            // Combine the title bar and main content
            VBox root = new VBox();
            root.setStyle("-fx-padding: 0; -fx-spacing: 0;");
            root.getChildren().addAll(mainContent);

            // Create the scene
            Scene scene = new Scene(root);

            // Set the stage properties
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception to debug the issue
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
