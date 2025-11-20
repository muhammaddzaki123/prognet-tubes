package prognet.network.server;

import prognet.common.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private static final Gson gson = new Gson();

    private Socket socket;
    private RoomManager roomManager;
    private BufferedReader in;
    private PrintWriter out;
    private Room currentRoom;
    private String playerName;
    private boolean running;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            LOGGER.info("Client connected: " + socket.getInetAddress());

            String inputLine;
            while (running && (inputLine = in.readLine()) != null) {
                handleMessage(inputLine);
            }

        } catch (IOException e) {
            LOGGER.warning("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleMessage(String json) {
        try {
            Message message = Message.fromJson(json);
            LOGGER.info("Received: " + message.getType() + " from " + playerName);

            switch (message.getType()) {
                case CREATE_ROOM:
                    handleCreateRoom(message);
                    break;
                case JOIN_ROOM:
                    handleJoinRoom(message);
                    break;
                case START_GAME:
                    handleStartGame(message);
                    break;
                case FLIP_CARD:
                    handleFlipCard(message);
                    break;
                case CHAT_MESSAGE:
                    handleChatMessage(message);
                    break;
                case DISCONNECT:
                    handleDisconnect();
                    break;
                default:
                    sendError("Unknown message type");
            }
        } catch (Exception e) {
            LOGGER.severe("Error handling message: " + e.getMessage());
            sendError("Server error: " + e.getMessage());
        }
    }

    private void handleCreateRoom(Message message) {
        playerName = message.getDataString("playerName");
        String gridSize = message.getDataString("gridSize");
        String theme = message.getDataString("theme");

        String roomCode = roomManager.createRoom(gridSize, theme);
        currentRoom = roomManager.getRoom(roomCode);
        currentRoom.addPlayer(this, playerName);

        JsonObject data = new JsonObject();
        data.addProperty("roomCode", roomCode);
        data.addProperty("playerName", playerName);
        data.addProperty("gridSize", gridSize);
        data.addProperty("theme", theme);

        sendMessage(new Message(MessageType.ROOM_CREATED, data).toJson());
    }

    private void handleJoinRoom(Message message) {
        playerName = message.getDataString("playerName");
        String roomCode = message.getDataString("roomCode");

        currentRoom = roomManager.getRoom(roomCode);

        if (currentRoom == null) {
            sendError("Room not found");
            return;
        }

        if (currentRoom.isFull()) {
            sendError("Room is full");
            return;
        }

        currentRoom.addPlayer(this, playerName);

        JsonObject data = new JsonObject();
        data.addProperty("roomCode", roomCode);
        data.addProperty("playerName", playerName);
        data.add("gameState", gson.toJsonTree(currentRoom.getGameState()));

        sendMessage(new Message(MessageType.ROOM_JOINED, data).toJson());

        // Notify other player
        JsonObject notifyData = new JsonObject();
        notifyData.addProperty("playerName", playerName);
        currentRoom.sendToOther(this, new Message(MessageType.PLAYER_JOINED, notifyData).toJson());
    }

    private void handleStartGame(Message message) {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }

        if (currentRoom.getHost() != this) {
            sendError("Only host can start the game");
            return;
        }

        if (!currentRoom.isFull()) {
            sendError("Waiting for second player");
            return;
        }

        currentRoom.startGame();

        JsonObject data = new JsonObject();
        data.add("gameState", gson.toJsonTree(currentRoom.getGameState()));

        currentRoom.broadcast(new Message(MessageType.GAME_STARTED, data).toJson());
    }

    private void handleFlipCard(Message message) {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }

        int cardId = message.getDataInt("cardId");
        int playerNumber = currentRoom.getPlayerNumber(this);

        if (!currentRoom.flipCard(cardId, playerNumber)) {
            sendError("Cannot flip this card");
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("cardId", cardId);
        data.addProperty("playerNumber", playerNumber);

        currentRoom.broadcast(new Message(MessageType.CARD_FLIPPED, data).toJson());

        // Check if this is the second flipped card
        long flippedCount = currentRoom.getGameState().getCards().stream()
                .filter(card -> card.isFlipped() && !card.isMatched())
                .count();

        if (flippedCount == 2) {
            // Find the two flipped cards
            int card1 = -1, card2 = -1;
            for (int i = 0; i < currentRoom.getGameState().getCards().size(); i++) {
                Card card = currentRoom.getGameState().getCards().get(i);
                if (card.isFlipped() && !card.isMatched()) {
                    if (card1 == -1)
                        card1 = i;
                    else
                        card2 = i;
                }
            }

            // Delay to show both cards
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            boolean isMatch = currentRoom.checkMatch(card1, card2);

            JsonObject matchData = new JsonObject();
            matchData.addProperty("card1Id", card1);
            matchData.addProperty("card2Id", card2);
            matchData.addProperty("isMatch", isMatch);
            matchData.add("gameState", gson.toJsonTree(currentRoom.getGameState()));

            MessageType type = isMatch ? MessageType.MATCH_FOUND : MessageType.NO_MATCH;
            currentRoom.broadcast(new Message(type, matchData).toJson());

            if (currentRoom.getGameState().isGameOver()) {
                JsonObject gameOverData = new JsonObject();
                gameOverData.add("gameState", gson.toJsonTree(currentRoom.getGameState()));
                currentRoom.broadcast(new Message(MessageType.GAME_OVER, gameOverData).toJson());
            }
        }
    }

    private void handleChatMessage(Message message) {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }

        String chatMessage = message.getDataString("message");

        JsonObject data = new JsonObject();
        data.addProperty("sender", playerName);
        data.addProperty("message", chatMessage);

        currentRoom.broadcast(new Message(MessageType.CHAT_MESSAGE, data).toJson());
    }

    private void handleDisconnect() {
        cleanup();
    }

    private void cleanup() {
        running = false;

        if (currentRoom != null) {
            currentRoom.removePlayer(this);

            JsonObject data = new JsonObject();
            data.addProperty("playerName", playerName);
            currentRoom.sendToOther(this, new Message(MessageType.PLAYER_LEFT, data).toJson());

            if (currentRoom.isEmpty()) {
                roomManager.removeRoom(currentRoom.getRoomCode());
            }
        }

        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            LOGGER.warning("Error closing resources: " + e.getMessage());
        }

        LOGGER.info("Client handler stopped for: " + playerName);
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void sendError(String errorMessage) {
        JsonObject data = new JsonObject();
        data.addProperty("message", errorMessage);
        sendMessage(new Message(MessageType.ERROR, data).toJson());
    }
}
