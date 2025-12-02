package prognet.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import prognet.App;
import prognet.util.NetworkConfig;

public class HomeController {

    @FXML
    private Button createRoomBtn;

    @FXML
    private Button joinRoomBtn;

    @FXML
    private Button howToPlayBtn;

    @FXML
    private Button configNetworkBtn;

    @FXML
    public void initialize() {
        // Setup button hover effects
        setupButtonHover(createRoomBtn, "#10B981", "#059669");
        setupButtonHover(joinRoomBtn, "#3B82F6", "#2563EB");
        setupButtonHoverOutline(howToPlayBtn);
        setupButtonHoverOutline(configNetworkBtn);
    }

    private void setupButtonHover(Button button, String normalColor, String hoverColor) {
        if (button == null) {
            return;
        }

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40 15 40;";

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; " + baseStyle + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + normalColor + "; " + baseStyle);
        });
    }

    private void setupButtonHoverOutline(Button button) {
        if (button == null) {
            return;
        }

        String baseStyle = "-fx-font-size: 16; -fx-font-weight: bold; -fx-border-color: #8B5CF6; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40 15 40;";

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #8B5CF6; -fx-text-fill: white;" + baseStyle + " -fx-scale-x: 1.02; -fx-scale-y: 1.02; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B5CF6; " + baseStyle);
        });
    }

    @FXML
    private void onCreateRoom() throws IOException {
        App.setRoot("createroom");
    }

    @FXML
    private void onJoinRoom() throws IOException {
        App.setRoot("joinroom");
    }

    @FXML
    private void onHowToPlay() {
        showAlert("How to Play",
                "🎮 Memory Game Instructions:\n\n"
                + "1. Find matching pairs of animal cards\n"
                + "2. Click on cards to flip them\n"
                + "3. Match all pairs to win!\n"
                + "4. Play with friends on the same WiFi\n\n"
                + "Have fun!");
    }

    @FXML
    private void onConfigureNetwork() {
        NetworkConfig config = NetworkConfig.getInstance();

        // Create custom dialog
        Alert dialog = new Alert(AlertType.CONFIRMATION);
        dialog.setTitle("Network Configuration");
        dialog.setHeaderText("Configure Server Connection");

        // Create the grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField hostField = new TextField(config.getServerHost());
        hostField.setPromptText("localhost or IP address");
        TextField portField = new TextField(String.valueOf(config.getServerPort()));
        portField.setPromptText("5000");

        grid.add(new Label("Server Host:"), 0, 0);
        grid.add(hostField, 1, 0);
        grid.add(new Label("Server Port:"), 0, 1);
        grid.add(portField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Add Reset button
        ButtonType resetButtonType = new ButtonType("Reset to Default");
        dialog.getButtonTypes().add(resetButtonType);

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Save configuration
                String host = hostField.getText().trim();
                String portText = portField.getText().trim();

                try {
                    int port = Integer.parseInt(portText);
                    if (port < 1 || port > 65535) {
                        showAlert("Invalid Port", "⚠ Port must be between 1-65535");
                        return;
                    }

                    if (host.isEmpty()) {
                        showAlert("Invalid Host", "⚠ Host cannot be empty");
                        return;
                    }

                    config.setServerHost(host);
                    config.setServerPort(port);
                    config.saveConfig();

                    showAlert("Success", "✓ Network configuration saved successfully!\n\nHost: " + host + "\nPort: " + port);
                    System.out.println("Network config saved - Host: " + host + ", Port: " + port);
                } catch (NumberFormatException e) {
                    showAlert("Invalid Port", "⚠ Port must be a number");
                }
            } else if (response == resetButtonType) {
                // Reset to defaults
                config.resetToDefaults();
                showAlert("Reset Complete", "✓ Network configuration reset to default\n\nHost: " + config.getServerHost() + "\nPort: " + config.getServerPort());
                System.out.println("Network config reset to defaults");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
