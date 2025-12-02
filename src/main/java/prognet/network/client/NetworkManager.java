package prognet.network.client;

import java.util.function.Consumer;
import java.util.logging.Logger;

import prognet.common.Message;

/**
 * Singleton NetworkManager that manages the GameClient lifecycle and provides
 * centralized access to network functionality across the application.
 */
public class NetworkManager {

    private static final Logger LOGGER = Logger.getLogger(NetworkManager.class.getName());
    private static NetworkManager instance;

    private GameClient client;
    private String currentPlayerName;
    private String currentRoomCode;
    private boolean isHost;

    private NetworkManager() {
        // Private constructor for singleton
    }

    /**
     * Get the singleton instance of NetworkManager
     */
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    /**
     * Initialize and connect the GameClient
     *
     * @return true if connection successful, false otherwise
     */
    public boolean connect() {
        if (client != null && client.isConnected()) {
            LOGGER.info("Already connected to server");
            return true;
        }

        client = new GameClient();
        boolean connected = client.autoConnect();

        if (connected) {
            LOGGER.info("Successfully connected to game server at " + client.getServerIP());
        } else {
            LOGGER.warning("Failed to connect to game server");
        }

        return connected;
    }

    /**
     * Connect to a specific server IP
     */
    public boolean connect(String serverIP) {
        if (client != null && client.isConnected()) {
            LOGGER.info("Already connected to server");
            return true;
        }

        client = new GameClient();
        boolean connected = client.connect(serverIP);

        if (connected) {
            LOGGER.info("Successfully connected to game server at " + serverIP);
        } else {
            LOGGER.warning("Failed to connect to game server at " + serverIP);
        }

        return connected;
    }

    /**
     * Get the GameClient instance
     */
    public GameClient getClient() {
        return client;
    }

    /**
     * Check if connected to server
     */
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    /**
     * Set the message handler for incoming messages
     */
    public void setMessageHandler(Consumer<Message> handler) {
        if (client != null) {
            client.setMessageHandler(handler);
        }
    }

    /**
     * Disconnect from the server
     */
    public void disconnect() {
        if (client != null) {
            client.disconnect();
            LOGGER.info("Disconnected from server");
        }
        currentPlayerName = null;
        currentRoomCode = null;
        isHost = false;
    }

    /**
     * Create a room as host
     */
    public void createRoom(String playerName, String gridSize, String theme) {
        if (client != null && client.isConnected()) {
            this.currentPlayerName = playerName;
            this.isHost = true;
            client.createRoom(playerName, gridSize, theme);
        } else {
            LOGGER.warning("Cannot create room: not connected to server");
        }
    }

    /**
     * Join an existing room
     */
    public void joinRoom(String playerName, String roomCode) {
        if (client != null && client.isConnected()) {
            this.currentPlayerName = playerName;
            this.currentRoomCode = roomCode;
            this.isHost = false;
            client.joinRoom(playerName, roomCode);
        } else {
            LOGGER.warning("Cannot join room: not connected to server");
        }
    }

    /**
     * Start the game (host only)
     */
    public void startGame() {
        if (client != null && client.isConnected() && isHost) {
            client.startGame();
        } else {
            LOGGER.warning("Cannot start game: not connected or not host");
        }
    }

    /**
     * Flip a card during the game
     */
    public void flipCard(int cardId) {
        if (client != null && client.isConnected()) {
            client.flipCard(cardId);
        } else {
            LOGGER.warning("Cannot flip card: not connected to server");
        }
    }

    /**
     * Send a chat message
     */
    public void sendChatMessage(String message) {
        if (client != null && client.isConnected()) {
            client.sendChatMessage(message);
        } else {
            LOGGER.warning("Cannot send chat: not connected to server");
        }
    }

    /**
     * Vote for rematch
     */
    public void voteRematch(boolean wantsRematch) {
        if (client != null && client.isConnected()) {
            client.voteRematch(wantsRematch);
        } else {
            LOGGER.warning("Cannot vote for rematch: not connected to server");
        }
    }

    /**
     * Leave to home (notifies server)
     */
    public void leaveToHome() {
        if (client != null && client.isConnected()) {
            client.leaveToHome();
        } else {
            LOGGER.warning("Cannot leave to home: not connected to server");
        }
    }

    // Getters
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public String getCurrentRoomCode() {
        return currentRoomCode;
    }

    public void setCurrentRoomCode(String roomCode) {
        this.currentRoomCode = roomCode;
    }

    public boolean isHost() {
        return isHost;
    }

    public String getServerIP() {
        return client != null ? client.getServerIP() : null;
    }

    /**
     * Reset the NetworkManager state (useful for cleanup or reconnection)
     */
    public void reset() {
        disconnect();
        client = null;
    }
}
