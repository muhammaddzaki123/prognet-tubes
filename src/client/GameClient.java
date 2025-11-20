package client;

import common.*;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class GameClient {
    private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
    private static final int PORT = 5000;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<Message> messageHandler;
    private boolean connected;
    private String serverIP;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GameClient() {
        this.connected = false;
    }

    public String getServerIP() {
        return serverIP;
    }

    public boolean connect(String serverIP) {
        this.serverIP = serverIP;
        try {
            socket = new Socket(serverIP, PORT);
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
        // Try localhost first
        if (connect("localhost")) {
            return true;
        }

        // Try UDP discovery
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
        if (connected && out != null && !executorService.isShutdown()) {
            executorService.submit(() -> {
                out.println(message.toJson());
                LOGGER.info("Sent: " + message.getType());
            });
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
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("roomCode", roomCode);
        sendMessage(new Message(MessageType.JOIN_ROOM, data));
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
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
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
