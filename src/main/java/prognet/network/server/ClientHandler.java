package prognet.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import prognet.common.Card;
import prognet.common.Message;
import prognet.common.MessageType;

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
        System.out.println("=== CREATE_ROOM Request ===");
        playerName = message.getDataString("playerName");
        String gridSize = message.getDataString("gridSize");
        String theme = message.getDataString("theme");

        System.out.println("Player: " + playerName);
        System.out.println("Grid Size: " + gridSize);
        System.out.println("Theme: " + theme);

        String roomCode = roomManager.createRoom(gridSize, theme);
        System.out.println("Room created with code: " + roomCode);
        System.out.println("Total active rooms: " + roomManager.getRoomCount());

        currentRoom = roomManager.getRoom(roomCode);
        System.out.println("Retrieved room from manager: " + (currentRoom != null ? "SUCCESS" : "FAILED"));

        currentRoom.addPlayer(this, playerName);
        System.out.println("Player added to room: " + playerName);

        JsonObject data = new JsonObject();
        data.addProperty("roomCode", roomCode);
        data.addProperty("playerName", playerName);
        data.addProperty("gridSize", gridSize);
        data.addProperty("theme", theme);

        sendMessage(new Message(MessageType.ROOM_CREATED, data).toJson());
        System.out.println("ROOM_CREATED message sent to client");
        System.out.println("=== CREATE_ROOM Complete ===");
    }

    private void handleJoinRoom(Message message) {
        System.out.println("=== JOIN_ROOM Request ===");
        playerName = message.getDataString("playerName");
        String roomCode = message.getDataString("roomCode");

        System.out.println("Player: " + playerName);
        System.out.println("Room Code: " + roomCode);
        System.out.println("Total active rooms: " + roomManager.getRoomCount());

        currentRoom = roomManager.getRoom(roomCode);

        if (currentRoom == null) {
            System.out.println("ERROR: Room not found - " + roomCode);
            sendError("Room not found");
            return;
        }
        System.out.println("Room found: " + roomCode);

        if (currentRoom.isFull()) {
            System.out.println("ERROR: Room is full - " + roomCode);
            sendError("Room is full");
            return;
        }
        System.out.println("Room has space available");

        currentRoom.addPlayer(this, playerName);
        System.out.println("Player added to room: " + playerName);

        JsonObject data = new JsonObject();
        data.addProperty("roomCode", roomCode);
        data.addProperty("playerName", playerName);
        data.add("gameState", gson.toJsonTree(currentRoom.getGameState()));

        sendMessage(new Message(MessageType.ROOM_JOINED, data).toJson());
        System.out.println("ROOM_JOINED message sent to client");

        // Notify other player
        JsonObject notifyData = new JsonObject();
        notifyData.addProperty("playerName", playerName);
        currentRoom.sendToOther(this, new Message(MessageType.PLAYER_JOINED, notifyData).toJson());
        System.out.println("PLAYER_JOINED notification sent to other player");
        System.out.println("=== JOIN_ROOM Complete ===");
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
                    if (card1 == -1) {
                        card1 = i;
                    } else {
                        card2 = i;
                    }
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

            // Broadcast turn change if no match (turn was switched in checkMatch)
            if (!isMatch) {
                JsonObject turnData = new JsonObject();
                int currentTurn = currentRoom.getGameState().getCurrentTurn();
                String currentPlayer = currentTurn == 1
                        ? currentRoom.getGameState().getPlayer1Name()
                        : currentRoom.getGameState().getPlayer2Name();
                turnData.addProperty("currentTurn", currentPlayer);
                currentRoom.broadcast(new Message(MessageType.TURN_CHANGED, turnData).toJson());
                System.out.println("Turn changed to player " + currentTurn + " (" + currentPlayer + ")");
            }

            // Also broadcast score update
            JsonObject scoreData = new JsonObject();
            scoreData.addProperty("player1Score", currentRoom.getGameState().getPlayer1Score());
            scoreData.addProperty("player2Score", currentRoom.getGameState().getPlayer2Score());
            currentRoom.broadcast(new Message(MessageType.SCORE_UPDATE, scoreData).toJson());

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
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
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
