package client.gui;

import client.GameClient;
import common.*;
import com.google.gson.Gson;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Gson gson = new Gson();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameClient gameClient;

    // Screens
    private HomeScreen homeScreen;
    private CreateRoomScreen createRoomScreen;
    private JoinRoomScreen joinRoomScreen;
    private WaitingRoomScreen waitingRoomScreen;
    private GameBoardScreen gameBoardScreen;
    private GameResultScreen gameResultScreen;
    private HowToPlayScreen howToPlayScreen;

    // Game state
    private boolean isHost;
    private int playerNumber;

    public MainFrame() {
        setTitle("Memory Game - Multiplayer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize game client
        gameClient = new GameClient();
        gameClient.setMessageHandler(this::handleMessage);

        // Try to connect to server
        if (!gameClient.autoConnect()) {
            // Show connection dialog with instructions
            boolean connected = showConnectionDialog();
            if (!connected) {
                System.exit(1);
            }
        }

        // Initialize screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homeScreen = new HomeScreen(this);
        createRoomScreen = new CreateRoomScreen(this);
        joinRoomScreen = new JoinRoomScreen(this);
        waitingRoomScreen = new WaitingRoomScreen(this);
        gameBoardScreen = new GameBoardScreen(this);
        gameResultScreen = new GameResultScreen(this);
        howToPlayScreen = new HowToPlayScreen(this);

        mainPanel.add(homeScreen, "HOME");
        mainPanel.add(createRoomScreen, "CREATE_ROOM");
        mainPanel.add(joinRoomScreen, "JOIN_ROOM");
        mainPanel.add(waitingRoomScreen, "WAITING_ROOM");
        mainPanel.add(gameBoardScreen, "GAME_BOARD");
        mainPanel.add(gameResultScreen, "GAME_RESULT");
        mainPanel.add(howToPlayScreen, "HOW_TO_PLAY");

        add(mainPanel);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (gameClient != null) {
                gameClient.disconnect();
            }
        }));
    }

    public void showScreen(String screenName) {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(mainPanel, screenName);
        });
    }

    private void handleMessage(Message message) {
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
            case ERROR:
                handleError(message);
                break;
        }
    }

    private void handleRoomCreated(Message message) {
        String roomCode = message.getDataString("roomCode");
        isHost = true;
        playerNumber = 1;
        createRoomScreen.setRoomCode(roomCode);
    }

    private void handleRoomJoined(Message message) {
        String roomCode = message.getDataString("roomCode");
        isHost = false;
        playerNumber = 2;

        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        waitingRoomScreen.updateGameState(gameState, false);
        showScreen("WAITING_ROOM");
    }

    private void handlePlayerJoined(Message message) {
        String playerName = message.getDataString("playerName");
        if (isHost) {
            createRoomScreen.setPlayer2Joined(playerName);
        }
    }

    private void handleGameStarted(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        gameBoardScreen.initializeGame(gameState, playerNumber);
        showScreen("GAME_BOARD");
    }

    private void handleCardFlipped(Message message) {
        int cardId = message.getDataInt("cardId");
        gameBoardScreen.handleCardFlipped(cardId);
    }

    private void handleMatchResult(Message message) {
        boolean isMatch = message.getType() == MessageType.MATCH_FOUND;
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        gameBoardScreen.handleMatchResult(isMatch, gameState);
    }

    private void handleGameOver(Message message) {
        GameState gameState = gson.fromJson(message.getData().get("gameState"), GameState.class);
        showGameResult(gameState, playerNumber);
    }

    private void handlePlayerLeft(Message message) {
        String playerName = message.getDataString("playerName");
        UIUtils.showInfo(this, playerName + " has left the game.");
        showScreen("HOME");
    }

    private void handleError(Message message) {
        String errorMessage = message.getDataString("message");
        UIUtils.showError(this, errorMessage);
    }

    public void showGameResult(GameState gameState, int playerNumber) {
        gameResultScreen.showResult(gameState, playerNumber);
        showScreen("GAME_RESULT");
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    private boolean showConnectionDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 10));

        panel.add(new JLabel("❌ Cannot connect to server automatically"));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Enter Server IP Address:"));

        JTextField ipField = new JTextField("192.168.1.");
        panel.add(ipField);

        panel.add(new JLabel("💡 Find server IP with: ipconfig (Windows) or ifconfig (Mac/Linux)"));
        panel.add(new JLabel("📶 Both devices must be on the same network"));

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Connect to Server",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String serverIP = ipField.getText().trim();
                if (!serverIP.isEmpty()) {
                    if (gameClient.connect(serverIP)) {
                        UIUtils.showInfo(this, "Successfully connected to " + serverIP);
                        return true;
                    } else {
                        int retry = JOptionPane.showConfirmDialog(
                                this,
                                "Failed to connect to " + serverIP + "\n\nMake sure:\n" +
                                        "1. Server is running (run-server.bat)\n" +
                                        "2. IP address is correct\n" +
                                        "3. Firewall allows port 5000\n\n" +
                                        "Try again?",
                                "Connection Failed",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE);

                        if (retry != JOptionPane.YES_OPTION) {
                            return false;
                        }
                        // Loop continues for retry
                    }
                } else {
                    UIUtils.showError(this, "Please enter a valid IP address");
                }
            } else {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
