package prognet.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import javafx.application.Platform;
import prognet.common.Message;
import prognet.common.MessageType;
import prognet.util.NetworkConfig;

public class GameClient {

    private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<Message> messageHandler;
    private boolean connected;
    private String serverIP;
    private ExecutorService executorService;

    public GameClient() {
        this.connected = false;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public String getServerIP() {
        return serverIP;
    }

    public boolean connect(String serverIP) {
        this.serverIP = serverIP;
        int port = NetworkConfig.getInstance().getServerPort();

        // Recreate executor if it was shutdown
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
            System.out.println("Created new ExecutorService");
        }

        try {
            socket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            LOGGER.info("Connected to server: " + serverIP);

            // Start listening thread
            startListening();

            return true;
        } catch (IOException e) {
            LOGGER.severe("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public boolean autoConnect() {
        // Try configured host first
        String configuredHost = NetworkConfig.getInstance().getServerHost();
        if (connect(configuredHost)) {
            return true;
        }

        // Try UDP discovery if configured host fails
        String discoveredIP = UDPDiscoveryClient.discoverServer();
        if (discoveredIP != null) {
            return connect(discoveredIP);
        }

        return false;
    }

    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                String line;
                while (connected && (line = in.readLine()) != null) {
                    final String finalLine = line;
                    Platform.runLater(() -> {
                        try {
                            Message message = Message.fromJson(finalLine);
                            LOGGER.info("Received: " + message.getType());
                            if (messageHandler != null) {
                                messageHandler.accept(message);
                            }
                        } catch (Exception e) {
                            LOGGER.severe("Error processing message: " + e.getMessage());
                        }
                    });
                }
            } catch (IOException e) {
                if (connected) {
                    LOGGER.warning("Connection lost: " + e.getMessage());
                    Platform.runLater(() -> {
                        disconnect();
                        if (messageHandler != null) {
                            JsonObject data = new JsonObject();
                            data.addProperty("message", "Connection lost");
                            messageHandler.accept(new Message(MessageType.ERROR, data));
                        }
                    });
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void setMessageHandler(Consumer<Message> handler) {
        this.messageHandler = handler;
    }

    public void sendMessage(Message message) {
        System.out.println("GameClient.sendMessage called for: " + message.getType());
        System.out.println("  connected=" + connected + ", out=" + (out != null) + ", executorShutdown=" + executorService.isShutdown());

        if (connected && out != null && !executorService.isShutdown()) {
            executorService.submit(() -> {
                out.println(message.toJson());
                LOGGER.info("Sent: " + message.getType());
                System.out.println("Message sent: " + message.getType());
            });
        } else {
            System.out.println("WARNING: Message not sent! connected=" + connected + ", out=" + (out != null) + ", executorShutdown=" + executorService.isShutdown());
        }
    }

    public void createRoom(String playerName, String gridSize, String theme) {
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("gridSize", gridSize);
        data.addProperty("theme", theme);
        sendMessage(new Message(MessageType.CREATE_ROOM, data));
    }

    public void joinRoom(String playerName, String roomCode) {
        System.out.println("GameClient.joinRoom called: playerName=" + playerName + ", roomCode=" + roomCode);
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("roomCode", roomCode);
        Message message = new Message(MessageType.JOIN_ROOM, data);
        System.out.println("Created JOIN_ROOM message: " + message.toJson());
        sendMessage(message);
    }

    public void startGame() {
        JsonObject data = new JsonObject();
        sendMessage(new Message(MessageType.START_GAME, data));
    }

    public void flipCard(int cardId) {
        JsonObject data = new JsonObject();
        data.addProperty("cardId", cardId);
        sendMessage(new Message(MessageType.FLIP_CARD, data));
    }

    public void sendChatMessage(String chatMessage) {
        JsonObject data = new JsonObject();
        data.addProperty("message", chatMessage);
        sendMessage(new Message(MessageType.CHAT_MESSAGE, data));
    }

    public void disconnect() {
        try {
            if (out != null && connected) {
                JsonObject data = new JsonObject();
                sendMessage(new Message(MessageType.DISCONNECT, data));
            }
        } finally {
            connected = false;
            executorService.shutdown();
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
                LOGGER.info("Disconnected from server");
            } catch (IOException e) {
                LOGGER.warning("Error closing resources during disconnect: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
