package prognet.controller;

import java.io.IOException;

import com.google.gson.Gson;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import prognet.App;
import prognet.common.GameState;
import prognet.common.Message;
import prognet.network.client.NetworkManager;

public class WaitingRoomController implements App.DataReceiver {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

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
    private Label animatedDots;

    private String roomCode;
    private Timeline dotsAnimation;
    private NetworkManager networkManager;
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        networkManager = NetworkManager.getInstance();

        // Setup button hover effects
        setupButtonHover(backBtn);

        // Start animated dots
        startDotsAnimation();

        // Setup network message handler
        setupNetworkHandlers();
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof String) {
            setRoomCode((String) data);
        }
    }

    public void setRoomCode(String code) {
        this.roomCode = code;

        // Set each digit in the corresponding label
        if (code != null && code.length() == 6) {
            roomCode1.setText(String.valueOf(code.charAt(0)));
            roomCode2.setText(String.valueOf(code.charAt(1)));
            roomCode3.setText(String.valueOf(code.charAt(2)));
            roomCode4.setText(String.valueOf(code.charAt(3)));
            roomCode5.setText(String.valueOf(code.charAt(4)));
            roomCode6.setText(String.valueOf(code.charAt(5)));
        }
    }

    private void startDotsAnimation() {
        final String[] dotStates = {".", "..", "..."};
        final int[] currentState = {0};

        dotsAnimation = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            animatedDots.setText(dotStates[currentState[0]]);
            currentState[0] = (currentState[0] + 1) % dotStates.length;
        }));

        dotsAnimation.setCycleCount(Timeline.INDEFINITE);
        dotsAnimation.play();
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

    @FXML
    private void onBack() {
        // Stop animations
        if (dotsAnimation != null) {
            dotsAnimation.stop();
        }

        // Disconnect from server
        if (networkManager != null && networkManager.isConnected()) {
            networkManager.disconnect();
        }

        try {
            App.setRoot("home");
        } catch (IOException e) {
            showError("Navigation Error", "Failed to return to home screen");
        }
    }

    private void setupNetworkHandlers() {
        networkManager.setMessageHandler(message -> {
            handleNetworkMessage(message);
        });
    }

    private void handleNetworkMessage(Message message) {
        switch (message.getType()) {
            case GAME_STARTED:
                handleGameStarted(message);
                break;
            case PLAYER_LEFT:
                handlePlayerLeft(message);
                break;
            case ERROR:
                handleError(message);
                break;
            default:
                break;
        }
    }

    private void handleGameStarted(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);

        Platform.runLater(() -> {
            cleanup();
            try {
                App.setRoot("gameboard", gameState);
            } catch (IOException e) {
                showError("Navigation Error", "Failed to start game");
            }
        });
    }

    private void handlePlayerLeft(Message message) {
        Platform.runLater(() -> {
            showError("Player Left", "The other player has left the room");
        });
    }

    private void handleError(Message message) {
        String errorMessage = message.getData().get("message").getAsString();
        Platform.runLater(() -> {
            showError("Error", errorMessage);
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cleanup() {
        if (dotsAnimation != null) {
            dotsAnimation.stop();
        }
    }
}
