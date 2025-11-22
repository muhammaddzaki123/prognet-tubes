package prognet.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import prognet.App;

public class HomeController {

    @FXML
    private Button createRoomBtn;

    @FXML
    private Button joinRoomBtn;

    @FXML
    private Button howToPlayBtn;

    @FXML
    public void initialize() {
        // Setup button hover effects
        setupButtonHover(createRoomBtn, "#10B981", "#059669");
        setupButtonHover(joinRoomBtn, "#3B82F6", "#2563EB");
        setupButtonHoverOutline(howToPlayBtn);
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
    private void onJoinRoom() {
        showAlert("Join Room", "Joining a room...\nThis feature will be implemented soon!");
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
