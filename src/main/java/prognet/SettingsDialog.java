package prognet;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SettingsDialog {

    public static void show(StackPane rootContainer) {
        try {
            // Create semi-transparent overlay background
            Rectangle overlay = new Rectangle();
            overlay.setFill(Color.rgb(0, 0, 0, 0.5));
            overlay.widthProperty().bind(rootContainer.widthProperty());
            overlay.heightProperty().bind(rootContainer.heightProperty());

            // Load settings FXML
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("settings.fxml"));
            Parent settingsContent = loader.load();

            // Get controller and set close handler
            SettingsController controller = loader.getController();
            controller.setOnClose(() -> {
                rootContainer.getChildren().remove(overlay);
                rootContainer.getChildren().remove(settingsContent);
            });

            // Add click handler to overlay to close settings
            overlay.setOnMouseClicked(event -> {
                rootContainer.getChildren().remove(overlay);
                rootContainer.getChildren().remove(settingsContent);
            });

            // Add overlay and settings to root container
            rootContainer.getChildren().add(overlay);
            rootContainer.getChildren().add(settingsContent);

        } catch (IOException e) {
            System.err.println("Failed to load settings overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
