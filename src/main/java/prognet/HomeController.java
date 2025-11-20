package prognet;

import java.io.IOException;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class HomeController {

    @FXML
    private Label topLeftAnimal;

    @FXML
    private Label topRightAnimal;

    @FXML
    private Label bottomLeftAnimal;

    @FXML
    private Label bottomRightAnimal;

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

        // Animate top left animal (Tiger)
        animateFloat(topLeftAnimal, -30, 1000, 0);

        // Animate top right animal (Bird)
        animateFloat(topRightAnimal, -35, 1500, 500);

        // Animate bottom left animal (Sloth)
        animateFloat(bottomLeftAnimal, -32, 1200, 300);

        // Animate bottom right animal (Monkey)
        animateFloat(bottomRightAnimal, -28, 1800, 700);
    }

    private void animateFloat(Label label, double distance, int duration, int delay) {
        if (label == null) {
            return;
        }

        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), label);
        transition.setFromY(0);
        transition.setToY(distance);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setDelay(Duration.millis(delay));
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        // Enable cache for better performance
        label.setCache(true);
        label.setCacheHint(javafx.scene.CacheHint.SPEED);

        transition.play();
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

        String baseStyle = "-fx-text-fill: #8B5CF6; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-color: #8B5CF6; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40 15 40;";

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #F3E8FF; " + baseStyle + " -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; " + baseStyle);
        });
    }

    @FXML
    private void onCreateRoom() {
        showAlert("Create Room", "Creating a new room...\nThis feature will be implemented soon!");
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

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
