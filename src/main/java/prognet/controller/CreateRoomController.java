package prognet.controller;

import java.io.IOException;
import java.util.Random;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import prognet.App;
import prognet.common.GameState;
import prognet.common.Message;
import prognet.network.client.NetworkManager;
import prognet.util.SettingsOverlay;

public class CreateRoomController {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private TextField playerNameField;

    @FXML
    private Button createRoomBtn;

    @FXML
    private javafx.scene.layout.VBox roomInfoSection;

    @FXML
    private Label player1NameLabel;

    @FXML
    private Label player2NameLabel;

    @FXML
    private Label player2StatusLabel;

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
    private NetworkManager networkManager;
    private Gson gson = new Gson();
    private boolean roomCreated = false;
    private boolean player2Joined = false;

    @FXML
    public void initialize() {
        System.out.println("CreateRoomController: initialize called");
        networkManager = NetworkManager.getInstance();

        // Disable start game button initially
        startGameBtn.setDisable(true);

        // Generate room code display (will be replaced by server)
        generateRoomCode();
        System.out.println("CreateRoomController: Initial client-side room code: " + roomCode);

        // Setup button hover effects
        setupButtonHover(backBtn);
        setupButtonHover(settingsBtn);
        setupButtonHover(copyCodeBtn);
        setupStartGameButtonHover();

        // Setup network message handler
        System.out.println("CreateRoomController: Setting up network handlers");
        setupNetworkHandlers();

        // Connect to server if not already connected
        if (!networkManager.isConnected()) {
            connectToServer();
        }
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
        // Disconnect from server if room was created
        if (roomCreated && networkManager.isConnected()) {
            networkManager.disconnect();
        }

        try {
            App.setRoot("home");
        } catch (IOException e) {
            showError("Navigation Error", "Failed to return to home screen");
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
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void onCreateRoom() {
        System.out.println("CreateRoomController: onCreateRoom called");
        String playerName = playerNameField.getText().trim();

        if (playerName.isEmpty()) {
            showError("Invalid Name", "Please enter your name");
            return;
        }

        if (!networkManager.isConnected()) {
            showError("Connection Error", "Not connected to server");
            return;
        }

        // Re-setup message handler right before creating room to ensure it's active
        // (in case another controller overwrote it)
        System.out.println("CreateRoomController: Re-setting up message handler before creating room");
        setupNetworkHandlers();

        // Get settings from SettingsOverlay (default values for now)
        String gridSize = "4x4";
        String theme = "animals";

        System.out.println("CreateRoomController: Creating room - Player: " + playerName + ", GridSize: " + gridSize + ", Theme: " + theme);
        // Create room
        networkManager.createRoom(playerName, gridSize, theme);

        // Show room info section and hide create button
        playerNameField.setDisable(true);
        createRoomBtn.setVisible(false);
        createRoomBtn.setManaged(false);
        roomInfoSection.setVisible(true);
        roomInfoSection.setManaged(true);

        System.out.println("CreateRoomController: Waiting for server response...");
    }

    @FXML
    private void onStartGame() {
        if (!player2Joined) {
            showError("Cannot Start", "Waiting for Player 2 to join");
            return;
        }

        if (!networkManager.isConnected()) {
            showError("Connection Error", "Not connected to server");
            return;
        }

        // Send start game message to server
        networkManager.startGame();
    }

    private void connectToServer() {
        boolean connected = networkManager.connect();

        if (!connected) {
            Platform.runLater(() -> {
                showError("Connection Failed",
                        "Could not connect to game server. Please make sure the server is running.");
            });
        }
    }

    private void setupNetworkHandlers() {
        networkManager.setMessageHandler(message -> {
            handleNetworkMessage(message);
        });
    }

    private void handleNetworkMessage(Message message) {
        System.out.println("CreateRoomController received message: " + message.getType());
        switch (message.getType()) {
            case ROOM_CREATED:
                handleRoomCreated(message);
                break;
            case PLAYER_JOINED:
                handlePlayerJoined(message);
                break;
            case GAME_STARTED:
                handleGameStarted(message);
                break;
            case ERROR:
                handleError(message);
                break;
            case PLAYER_LEFT:
                handlePlayerLeft(message);
                break;
            default:
                System.out.println("Unhandled message type in CreateRoomController: " + message.getType());
                break;
        }
    }

    private void handleRoomCreated(Message message) {
        System.out.println("CreateRoomController: handleRoomCreated called");
        String serverRoomCode = message.getData().get("roomCode").getAsString();
        System.out.println("CreateRoomController: Server room code: " + serverRoomCode);
        System.out.println("CreateRoomController: Old client room code: " + roomCode);

        roomCode = serverRoomCode;
        roomCreated = true;

        // Update UI with server-generated room code
        Platform.runLater(() -> {
            System.out.println("CreateRoomController: Updating UI with room code: " + roomCode);
            roomCode1.setText(String.valueOf(roomCode.charAt(0)));
            roomCode2.setText(String.valueOf(roomCode.charAt(1)));
            roomCode3.setText(String.valueOf(roomCode.charAt(2)));
            roomCode4.setText(String.valueOf(roomCode.charAt(3)));
            roomCode5.setText(String.valueOf(roomCode.charAt(4)));
            roomCode6.setText(String.valueOf(roomCode.charAt(5)));

            // Update player 1 name
            player1NameLabel.setText(playerNameField.getText() + " (You)");
            System.out.println("CreateRoomController: UI updated with room code: " + roomCode);
        });

        System.out.println("Room created successfully: " + roomCode);
    }

    private void handlePlayerJoined(Message message) {
        String playerName = message.getData().get("playerName").getAsString();
        player2Joined = true;

        Platform.runLater(() -> {
            player2NameLabel.setText(playerName);
            player2StatusLabel.setText("Connected");
            startGameBtn.setDisable(false);

            // Show notification
            // Alert alert = new Alert(Alert.AlertType.INFORMATION);
            // alert.setTitle("Player Joined");
            // alert.setHeaderText(null);
            // alert.setContentText(playerName + " has joined the room!");
            // alert.show();
        });

        System.out.println("Player 2 joined: " + playerName);
    }

    private void handleGameStarted(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);

        Platform.runLater(() -> {
            try {
                App.setRoot("gameboard", gameState);
            } catch (IOException e) {
                showError("Navigation Error", "Failed to start game");
            }
        });
    }

    private void handlePlayerLeft(Message message) {
        player2Joined = false;

        Platform.runLater(() -> {
            player2NameLabel.setText("Waiting for player...");
            player2StatusLabel.setText("Not Connected");
            startGameBtn.setDisable(true);

            showError("Player Left", "Player 2 has left the room");
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
}
