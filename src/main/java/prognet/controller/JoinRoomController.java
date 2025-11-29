package prognet.controller;

import java.io.IOException;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import prognet.App;
import prognet.common.GameState;
import prognet.common.Message;
import prognet.network.client.NetworkManager;

public class JoinRoomController {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

    @FXML
    private TextField playerNameField;

    @FXML
    private TextField digit1;

    @FXML
    private TextField digit2;

    @FXML
    private TextField digit3;

    @FXML
    private TextField digit4;

    @FXML
    private TextField digit5;

    @FXML
    private TextField digit6;

    @FXML
    private TextField fullCodeInput;

    @FXML
    private Button joinBtn;

    private TextField[] digitFields;
    private NetworkManager networkManager;
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        networkManager = NetworkManager.getInstance();

        // Initialize digit fields array
        digitFields = new TextField[]{digit1, digit2, digit3, digit4, digit5, digit6};

        // Setup digit input behavior
        setupDigitInputs();

        // Setup full code input behavior
        setupFullCodeInput();

        // Setup button hover effects
        setupButtonHover(backBtn);
        setupJoinButtonHover();

        // Setup network message handler
        // IMPORTANT: Set handler in initialize to catch all messages
        System.out.println("JoinRoomController: Setting up message handler in initialize");
        setupNetworkHandlers();

        // Connect to server if not already connected
        if (!networkManager.isConnected()) {
            connectToServer();
        }

        // Focus first digit on load
        Platform.runLater(() -> digit1.requestFocus());
    }

    private void setupDigitInputs() {
        for (int i = 0; i < digitFields.length; i++) {
            TextField field = digitFields[i];
            final int index = i;

            // Limit to single digit
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 1) {
                    field.setText(newValue.substring(0, 1));
                }
                // Only allow digits
                if (!newValue.matches("\\d*")) {
                    field.setText(oldValue);
                }
                // Auto-focus next field
                if (newValue.length() == 1 && index < digitFields.length - 1) {
                    digitFields[index + 1].requestFocus();
                }
                // Update full code input and button state
                updateFullCodeFromDigits();
                updateJoinButtonState();
            });

            // Handle backspace to move to previous field
            field.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("BACK_SPACE")) {
                    if (field.getText().isEmpty() && index > 0) {
                        digitFields[index - 1].requestFocus();
                        digitFields[index - 1].selectAll();
                    }
                }
            });
        }
    }

    private void setupFullCodeInput() {
        // Limit to 6 digits
        fullCodeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 6) {
                fullCodeInput.setText(newValue.substring(0, 6));
            }
            // Only allow digits
            if (!newValue.matches("\\d*")) {
                fullCodeInput.setText(oldValue);
            }
            // Update individual digits
            updateDigitsFromFullCode();
            updateJoinButtonState();
        });
    }

    private void updateFullCodeFromDigits() {
        StringBuilder code = new StringBuilder();
        for (TextField field : digitFields) {
            code.append(field.getText());
        }
        fullCodeInput.setText(code.toString());
    }

    private void updateDigitsFromFullCode() {
        String code = fullCodeInput.getText();
        for (int i = 0; i < digitFields.length; i++) {
            if (i < code.length()) {
                digitFields[i].setText(String.valueOf(code.charAt(i)));
            } else {
                digitFields[i].setText("");
            }
        }
    }

    private void updateJoinButtonState() {
        String code = fullCodeInput.getText();
        boolean isValid = code.length() == 6;
        joinBtn.setDisable(!isValid);
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

    private void setupJoinButtonHover() {
        if (joinBtn == null) {
            return;
        }

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40 15 40;";

        joinBtn.setOnMouseEntered(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle("-fx-background-color: #2563EB; " + baseStyle
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
            }
        });

        joinBtn.setOnMouseExited(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle("-fx-background-color: #3B82F6; " + baseStyle);
            }
        });
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to return to home screen", AlertType.ERROR);
        }
    }

    @FXML
    private void onJoinRoom() {
        String playerName = playerNameField.getText().trim();
        String roomCode = fullCodeInput.getText();

        if (playerName.isEmpty()) {
            showAlert("Invalid Name", "Please enter your name", AlertType.WARNING);
            return;
        }

        if (roomCode.length() != 6) {
            showAlert("Invalid Code", "Please enter a valid 6-digit room code.", AlertType.WARNING);
            return;
        }

        if (!networkManager.isConnected()) {
            showAlert("Connection Error", "Not connected to server. Please try again.", AlertType.ERROR);
            return;
        }

        // Re-setup message handler right before joining to ensure it's active
        // (in case another controller overwrote it)
        System.out.println("JoinRoomController: Re-setting up message handler before joining room");
        setupNetworkHandlers();

        // Join room via network
        System.out.println("JoinRoomController: Joining room " + roomCode + " as " + playerName);
        networkManager.joinRoom(playerName, roomCode);

        // Disable button while waiting for response
        joinBtn.setDisable(true);
        joinBtn.setText("Joining...");
    }

    private void connectToServer() {
        boolean connected = networkManager.connect();

        if (!connected) {
            Platform.runLater(() -> {
                showAlert("Connection Failed",
                        "Could not connect to game server. Please make sure the server is running.",
                        AlertType.ERROR);
            });
        }
    }

    private void setupNetworkHandlers() {
        networkManager.setMessageHandler(message -> {
            handleNetworkMessage(message);
        });
    }

    private void handleNetworkMessage(Message message) {
        System.out.println("JoinRoomController received message: " + message.getType());
        switch (message.getType()) {
            case ROOM_JOINED:
                handleRoomJoined(message);
                break;
            case GAME_STARTED:
                handleGameStarted(message);
                break;
            case ERROR:
                handleError(message);
                break;
            default:
                System.out.println("Unhandled message type in JoinRoomController: " + message.getType());
                break;
        }
    }

    private void handleRoomJoined(Message message) {
        System.out.println("ROOM_JOINED message received!");
        String roomCode = message.getData().get("roomCode").getAsString();
        System.out.println("Navigating to waiting room with code: " + roomCode);

        Platform.runLater(() -> {
            try {
                // Navigate to waiting room
                App.setRoot("waitingroom", roomCode);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Navigation Error", "Failed to join room", AlertType.ERROR);
                // Re-enable button
                joinBtn.setDisable(false);
                joinBtn.setText("🚪  Join Room");
            }
        });
    }

    private void handleGameStarted(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);

        Platform.runLater(() -> {
            try {
                App.setRoot("gameboard", gameState);
            } catch (IOException e) {
                showAlert("Navigation Error", "Failed to start game", AlertType.ERROR);
            }
        });
    }

    private void handleError(Message message) {
        String errorMessage = message.getData().get("message").getAsString();

        Platform.runLater(() -> {
            showAlert("Error", errorMessage, AlertType.ERROR);
            // Re-enable button
            joinBtn.setDisable(false);
            joinBtn.setText("🚪  Join Room");
        });
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
