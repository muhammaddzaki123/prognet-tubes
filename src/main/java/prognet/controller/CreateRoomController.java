package prognet.controller;

import java.io.IOException;
import java.util.Random;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import prognet.App;
import prognet.util.SettingsOverlay;

public class CreateRoomController {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Label roomCode1;

    @FXML
    private Label roomCode2;

    @FXML
    private Label roomCode3;

    @FXML
    private Label roomCode4;

    @FXML
    private Label roomCode5;

    @FXML
    private Label roomCode6;

    @FXML
    private Button copyCodeBtn;

    @FXML
    private Button startGameBtn;

    private String roomCode;

    @FXML
    public void initialize() {
        // Generate random 6-digit room code
        generateRoomCode();

        // Setup button hover effects
        setupButtonHover(backBtn);
        setupButtonHover(settingsBtn);
        setupButtonHover(copyCodeBtn);
        setupStartGameButtonHover();
    }

    private void generateRoomCode() {
        Random random = new Random();
        roomCode = String.format("%06d", random.nextInt(1000000));

        // Set each digit in the corresponding label
        roomCode1.setText(String.valueOf(roomCode.charAt(0)));
        roomCode2.setText(String.valueOf(roomCode.charAt(1)));
        roomCode3.setText(String.valueOf(roomCode.charAt(2)));
        roomCode4.setText(String.valueOf(roomCode.charAt(3)));
        roomCode5.setText(String.valueOf(roomCode.charAt(4)));
        roomCode6.setText(String.valueOf(roomCode.charAt(5)));
    }

    private void setupButtonHover(Button button) {
        if (button == null) {
            return;
        }

        button.setOnMouseEntered(e -> {
            button.setOpacity(0.7);
        });

        button.setOnMouseExited(e -> {
            button.setOpacity(1.0);
        });
    }

    private void setupStartGameButtonHover() {
        if (startGameBtn == null) {
            return;
        }

        startGameBtn.setOnMouseEntered(e -> {
            startGameBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #7C3AED, #DB2777); -fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 18 40 18 40; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        startGameBtn.setOnMouseExited(e -> {
            startGameBtn.setStyle(
                    "-fx-background-color: linear-gradient(to right, #8B5CF6, #EC4899); -fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 18 40 18 40;");
        });
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSettings() {
        SettingsOverlay.show(rootContainer);
    }

    @FXML
    private void onCopyCode() {
        // Copy room code to clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(roomCode);
        clipboard.setContent(content);

        // Show feedback to user
        String originalText = copyCodeBtn.getText();
        copyCodeBtn.setText("✓  Copied!");
        copyCodeBtn.setStyle(
                "-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 30 10 30; -fx-border-color: #10B981; -fx-border-width: 1; -fx-border-radius: 10;");

        // Reset button after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    copyCodeBtn.setText(originalText);
                    copyCodeBtn.setStyle(
                            "-fx-background-color: white; -fx-text-fill: #666666; -fx-font-size: 14; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 30 10 30; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 10;");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onStartGame() {
        // TODO: Implement start game functionality
        System.out.println("Starting game with room code: " + roomCode);
    }
}
