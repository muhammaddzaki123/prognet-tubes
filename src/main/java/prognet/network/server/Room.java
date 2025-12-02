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
        // Extended animal lists to support up to 8x8 grids (32 unique animals needed)
        animalsByTheme.put("animals",
                "tiger,sloth,toucan,orangutan,lemur,crocodile,redpanda,warthog,rhino,antelope,elephant,giraffe,lion,zebra,monkey,parrot,snake,bear,wolf,fox,deer,rabbit,kangaroo,koala,penguin,seal,dolphin,whale,shark,octopus,turtle,frog");
        animalsByTheme.put("jungle",
                "tiger,sloth,toucan,orangutan,lemur,crocodile,redpanda,warthog,monkey,parrot,snake,jaguar,panther,gorilla,chimpanzee,macaw,boa,python,anaconda,iguana,chameleon,frog,poison_dart_frog,howler_monkey,spider_monkey,capuchin,tamarin,coati,tapir,anteater,armadillo,peccary");
        animalsByTheme.put("forest",
                "sloth,redpanda,warthog,antelope,tiger,lemur,toucan,orangutan,bear,wolf,fox,deer,rabbit,squirrel,owl,eagle,hawk,badger,hedgehog,moose,elk,beaver,raccoon,skunk,porcupine,lynx,wildcat,marten,woodpecker,jay,chipmunk,opossum");
        animalsByTheme.put("savanna",
                "rhino,warthog,antelope,lemur,tiger,crocodile,orangutan,toucan,lion,elephant,giraffe,zebra,cheetah,leopard,hyena,wildebeest,buffalo,gazelle,impala,kudu,ostrich,vulture,eagle,secretary_bird,meerkat,mongoose,jackal,wild_dog,baboon,vervet_monkey,caracal,serval");
        animalsByTheme.put("ocean",
                "crocodile,toucan,redpanda,sloth,lemur,antelope,warthog,rhino,dolphin,whale,shark,octopus,turtle,seal,penguin,sea_lion,otter,walrus,jellyfish,starfish,crab,lobster,shrimp,clownfish,seahorse,manta_ray,stingray,swordfish,tuna,sailfish,barracuda,pufferfish");
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
                // Increment total wins for the winner
                if (gameState.getPlayer1Score() > gameState.getPlayer2Score()) {
                    gameState.incrementPlayer1TotalWins();
                } else if (gameState.getPlayer2Score() > gameState.getPlayer1Score()) {
                    gameState.incrementPlayer2TotalWins();
                }
                // If tie, no one gets a win point
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

    // Rematch handling
    public synchronized void voteRematch(int playerNumber, boolean wants) {
        if (playerNumber == 1) {
            gameState.setPlayer1WantsRematch(wants);
        } else if (playerNumber == 2) {
            gameState.setPlayer2WantsRematch(wants);
        }
    }

    public synchronized boolean bothPlayersWantRematch() {
        return gameState.isPlayer1WantsRematch() && gameState.isPlayer2WantsRematch();
    }

    public synchronized void resetForRematch() {
        // Reset round scores and game state
        gameState.resetRoundScores();
        gameState.resetRematchVotes();
        gameState.setGameOver(false);
        gameState.setCurrentTurn(1);

        // Keep total wins intact
        // Re-initialize cards
        initializeCards();
        gameState.setGameStarted(true);
    }

    public synchronized void resetTotalWins() {
        // Called when players return to home
        gameState.resetTotalWins();
    }
}
