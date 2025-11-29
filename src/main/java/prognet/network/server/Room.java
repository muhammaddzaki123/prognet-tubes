package prognet.network.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prognet.common.Card;
import prognet.common.GameState;

public class Room {

    private String roomCode;
    private GameState gameState;
    private ClientHandler host;
    private ClientHandler guest;
    private Map<String, String> animalsByTheme;

    public Room(String roomCode, String gridSize, String theme) {
        this.roomCode = roomCode;
        this.gameState = new GameState(roomCode, gridSize, theme);
        initAnimalsByTheme();
    }

    private void initAnimalsByTheme() {
        animalsByTheme = new HashMap<>();
        animalsByTheme.put("animals", "tiger,sloth,toucan,orangutan,lemur,crocodile,redpanda,warthog");
        animalsByTheme.put("jungle", "tiger,sloth,toucan,orangutan,lemur,crocodile,redpanda,warthog");
        animalsByTheme.put("forest", "sloth,redpanda,warthog,antelope,tiger,lemur,toucan,orangutan");
        animalsByTheme.put("savanna", "rhino,warthog,antelope,lemur,tiger,crocodile,orangutan,toucan");
        animalsByTheme.put("ocean", "crocodile,toucan,redpanda,sloth,lemur,antelope,warthog,rhino");
    }

    public synchronized boolean addPlayer(ClientHandler client, String playerName) {
        if (host == null) {
            host = client;
            gameState.setPlayer1Name(playerName);
            return true;
        } else if (guest == null) {
            guest = client;
            gameState.setPlayer2Name(playerName);
            return true;
        }
        return false;
    }

    public synchronized void removePlayer(ClientHandler client) {
        if (host == client) {
            host = null;
            gameState.setPlayer1Name(null);
        } else if (guest == client) {
            guest = null;
            gameState.setPlayer2Name(null);
        }
    }

    public boolean isFull() {
        return host != null && guest != null;
    }

    public boolean isEmpty() {
        return host == null && guest == null;
    }

    public void startGame() {
        initializeCards();
        gameState.setGameStarted(true);
    }

    private void initializeCards() {
        String theme = gameState.getTheme();
        System.out.println("Room.initializeCards: theme = " + theme);

        String animalList = animalsByTheme.get(theme);
        if (animalList == null) {
            System.out.println("WARNING: Theme '" + theme + "' not found, using default 'animals'");
            animalList = animalsByTheme.get("animals");
        }

        String[] animals = animalList.split(",");
        System.out.println("Available animals: " + Arrays.toString(animals));

        int gridSize = Integer.parseInt(gameState.getGridSize().split("x")[0]);
        int totalPairs = (gridSize * gridSize) / 2;
        System.out.println("Grid size: " + gridSize + "x" + gridSize + ", Total pairs: " + totalPairs);

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            String animal = animals[i % animals.length];
            cards.add(new Card(i * 2, animal));
            cards.add(new Card(i * 2 + 1, animal));
        }

        Collections.shuffle(cards);
        System.out.println("Cards shuffled");

        // Reassign IDs after shuffle
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            cards.set(i, new Card(i, card.getAnimal()));
        }

        gameState.setCards(cards);
    }

    public synchronized boolean flipCard(int cardId, int playerNumber) {
        if (gameState.getCurrentTurn() != playerNumber) {
            return false;
        }

        Card card = gameState.getCards().get(cardId);
        if (card.isMatched() || card.isFlipped()) {
            return false;
        }

        card.setFlipped(true);
        return true;
    }

    public synchronized boolean checkMatch(int card1Id, int card2Id) {
        Card card1 = gameState.getCards().get(card1Id);
        Card card2 = gameState.getCards().get(card2Id);

        if (card1.getAnimal().equals(card2.getAnimal())) {
            card1.setMatched(true);
            card2.setMatched(true);
            gameState.incrementScore(gameState.getCurrentTurn());

            // Check if game is over
            boolean allMatched = gameState.getCards().stream().allMatch(Card::isMatched);
            if (allMatched) {
                gameState.setGameOver(true);
            }

            return true;
        } else {
            card1.setFlipped(false);
            card2.setFlipped(false);
            gameState.switchTurn();
            return false;
        }
    }

    public void broadcast(String message) {
        if (host != null) {
            host.sendMessage(message);
        }
        if (guest != null) {
            guest.sendMessage(message);
        }
    }

    public void sendToOther(ClientHandler sender, String message) {
        if (sender == host && guest != null) {
            guest.sendMessage(message);
        } else if (sender == guest && host != null) {
            host.sendMessage(message);
        }
    }

    public int getPlayerNumber(ClientHandler client) {
        if (client == host) {
            return 1;
        }
        if (client == guest) {
            return 2;
        }
        return 0;
    }

    // Getters
    public String getRoomCode() {
        return roomCode;
    }

    public GameState getGameState() {
        return gameState;
    }

    public ClientHandler getHost() {
        return host;
    }

    public ClientHandler getGuest() {
        return guest;
    }
}
