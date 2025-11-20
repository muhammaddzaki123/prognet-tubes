package client.gui.javafx;

import client.GameClient;
import common.*;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class ClientApp extends Application {
    private static final Gson gson = new Gson();

    private Stage primaryStage;
    private MainView mainView;
    private GameClient gameClient;

    // Game state
    private boolean isHost;
    private int playerNumber;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize game client
        gameClient = new GameClient();
        gameClient.setMessageHandler(this::handleMessage);

        // Initialize main view
        mainView = new MainView(this);

        Scene scene = new Scene(mainView, 800, 600);
        primaryStage.setTitle("Memory Game - Multiplayer");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (gameClient != null) {
                gameClient.disconnect();
            }
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // Connect after showing the window
        Platform.runLater(() -> {
            if (!gameClient.autoConnect()) {
                if (!showConnectionDialog()) {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public MainView getMainView() {
        return mainView;
    }

    private void handleMessage(Message message) {
        Platform.runLater(() -> {
            switch (message.getType()) {
                case ROOM_CREATED:
                    handleRoomCreated(message);
                    break;
                case ROOM_JOINED:
                    handleRoomJoined(message);
                    break;
                case PLAYER_JOINED:
                    handlePlayerJoined(message);
                    break;
                case GAME_STARTED:
                    handleGameStarted(message);
                    break;
                case CARD_FLIPPED:
                    handleCardFlipped(message);
                    break;
                case MATCH_FOUND:
                case NO_MATCH:
                    handleMatchResult(message);
                    break;
                case GAME_OVER:
                    handleGameOver(message);
                    break;
                case PLAYER_LEFT:
                    handlePlayerLeft(message);
                    break;
                case CHAT_MESSAGE:
                    handleChatMessage(message);
                    break;
                case ERROR:
                    handleError(message);
                    break;
            }
        });
    }

    private void handleRoomCreated(Message message) {
        String roomCode = message.getDataString("roomCode");
        isHost = true;
        playerNumber = 1;
        if (mainView.getCreateRoomView() != null) {
            mainView.getCreateRoomView().setRoomCode(roomCode);
        }
    }

    private void handleRoomJoined(Message message) {
        String roomCode = message.getDataString("roomCode");
        isHost = false;
        playerNumber = 2;

        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        if (mainView.getWaitingRoomView() != null) {
            mainView.getWaitingRoomView().updateGameState(gameState, false);
        }
        mainView.showView("WAITING_ROOM");
    }

    private void handlePlayerJoined(Message message) {
        String playerName = message.getDataString("playerName");
        if (isHost && mainView.getCreateRoomView() != null) {
            mainView.getCreateRoomView().setPlayer2Joined(playerName);
        }
    }

    private void handleGameStarted(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        if (mainView.getGameBoardView() != null) {
            mainView.getGameBoardView().initializeGame(gameState, playerNumber);
        }
        mainView.showView("GAME_BOARD");
    }

    private void handleCardFlipped(Message message) {
        int cardId = message.getDataInt("cardId");
        if (mainView.getGameBoardView() != null) {
            mainView.getGameBoardView().handleCardFlipped(cardId);
        }
    }

    private void handleMatchResult(Message message) {
        boolean isMatch = message.getType() == MessageType.MATCH_FOUND;
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        if (mainView.getGameBoardView() != null) {
            mainView.getGameBoardView().handleMatchResult(isMatch, gameState);
        }
    }

    private void handleGameOver(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        showGameResult(gameState, playerNumber);
    }

    private void handlePlayerLeft(Message message) {
        String playerName = message.getDataString("playerName");
        UIUtilsFX.showInfo(playerName + " has left the game.");
        mainView.showView("HOME");
    }

    private void handleChatMessage(Message message) {
        String sender = message.getDataString("sender");
        String chatMessage = message.getDataString("message");
        if (mainView.getGameBoardView() != null) {
            mainView.getGameBoardView().addChatMessage(sender, chatMessage);
        }
    }

    private void handleError(Message message) {
        String errorMessage = message.getDataString("message");
        UIUtilsFX.showError(errorMessage);
    }

    public void showGameResult(GameState gameState, int playerNumber) {
        if (mainView.getGameResultView() != null) {
            mainView.getGameResultView().showResult(gameState, playerNumber);
        }
        mainView.showView("GAME_RESULT");
    }

    public boolean showConnectionDialog() {
        TextInputDialog dialog = new TextInputDialog("192.168.1.");
        dialog.setTitle("Connect to Server");
        dialog.setHeaderText("❌ Cannot connect to server automatically\n\nBoth devices must be on the same network.");
        dialog.setContentText("Enter Server IP Address:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String serverIP = result.get().trim();
            if (!serverIP.isEmpty()) {
                if (gameClient.connect(serverIP)) {
                    UIUtilsFX.showInfo("Successfully connected to " + serverIP);
                    // Refresh home view if needed to show connected status
                    mainView.showView("HOME"); // Force refresh/re-init of home view
                    return true;
                } else {
                     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                     alert.setTitle("Connection Failed");
                     alert.setHeaderText("Failed to connect to " + serverIP);
                     alert.setContentText("Make sure:\n1. Server is running\n2. IP address is correct\n3. Firewall allows port 5000\n\nTry again?");

                     Optional<ButtonType> retry = alert.showAndWait();
                     if (retry.isPresent() && retry.get() == ButtonType.OK) {
                         return showConnectionDialog();
                     }
                }
            }
        }
        return false;
    }
}
