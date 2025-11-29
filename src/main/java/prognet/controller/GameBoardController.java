package prognet.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import prognet.App;
import prognet.common.Card;
import prognet.common.GameState;
import prognet.common.Message;
import prognet.network.client.NetworkManager;

public class GameBoardController implements App.DataReceiver {

    @FXML
    private Button leaveBtn;

    @FXML
    private Label roomCodeLabel;

    @FXML
    private VBox player1Card;

    @FXML
    private Label player1NameLabel;

    @FXML
    private Label player1SubtitleLabel;

    @FXML
    private Label player1ScoreLabel;

    @FXML
    private Label player1TurnLabel;

    @FXML
    private VBox player2Card;

    @FXML
    private Label player2NameLabel;

    @FXML
    private Label player2SubtitleLabel;

    @FXML
    private Label player2ScoreLabel;

    @FXML
    private Label player2TurnLabel;

    @FXML
    private Label turnIndicatorLabel;

    @FXML
    private GridPane cardGrid;

    // Game state from server
    private GameState gameState;
    private NetworkManager networkManager;

    private Button[][] cardButtons;
    private final Map<Integer, Button> cardButtonMap = new HashMap<>();
    private final Map<String, String> animalEmojis = new HashMap<>();

    @FXML
    public void initialize() {
        networkManager = NetworkManager.getInstance();
        initAnimalEmojis();
        setupButtonHover(leaveBtn);

        // Setup network message handler
        setupNetworkHandlers();
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof GameState state) {
            this.gameState = state;
            initializeGameFromState();
        } else if (data instanceof String roomCode) {
            // Legacy support if only room code is passed
            roomCodeLabel.setText(roomCode);
        }
    }

    private void initializeGameFromState() {
        if (gameState == null) {
            return;
        }

        // Update UI with game state
        roomCodeLabel.setText(gameState.getRoomCode());
        player1NameLabel.setText(gameState.getPlayer1Name());
        player2NameLabel.setText(gameState.getPlayer2Name());

        // Setup game board
        setupGameBoard();

        // Update scores and turn
        updateScores();
        updateTurnIndicator();
    }

    private void initAnimalEmojis() {
        animalEmojis.put("tiger", "🐯");
        animalEmojis.put("sloth", "🦥");
        animalEmojis.put("toucan", "🦜");
        animalEmojis.put("orangutan", "🦧");
        animalEmojis.put("lemur", "🐒");
        animalEmojis.put("crocodile", "🐊");
        animalEmojis.put("redpanda", "🦝");
        animalEmojis.put("warthog", "🐗");
        animalEmojis.put("rhino", "🦏");
        animalEmojis.put("antelope", "🦌");
    }

    private void setupGameBoard() {
        cardGrid.getChildren().clear();
        cardButtonMap.clear();

        int gridSize = getGridSize();
        cardButtons = new Button[gridSize][gridSize];

        int cardIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (cardIndex >= gameState.getCards().size()) {
                    break;
                }

                final Card card = gameState.getCards().get(cardIndex);

                // Create card button
                Button cardButton = createCardButton(card);

                cardButtons[row][col] = cardButton;
                cardButtonMap.put(card.getId(), cardButton);
                cardGrid.add(cardButton, col, row);
                cardIndex++;
            }
        }
    }

    private Button createCardButton(Card card) {
        Button cardButton = new Button();
        updateCardButtonAppearance(cardButton, card);

        cardButton.setPrefSize(100, 100);
        cardButton.setMaxSize(100, 100);
        cardButton.setMinSize(100, 100);

        // Add hover effect
        cardButton.setOnMouseEntered(e -> {
            if (!card.isFlipped() && !card.isMatched() && isMyTurn()) {
                cardButton.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #D946EF, #A855F7);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 48;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 12;"
                        + "-fx-cursor: hand;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);"
                        + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"
                );
            }
        });

        cardButton.setOnMouseExited(e -> {
            if (!card.isFlipped() && !card.isMatched()) {
                cardButton.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #E879F9, #C084FC);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 48;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 12;"
                        + "-fx-cursor: hand;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
                );
            }
        });

        // Add click handler
        cardButton.setOnAction(e -> handleCardClick(card));

        return cardButton;
    }

    private void updateCardButtonAppearance(Button button, Card card) {
        String emoji = animalEmojis.getOrDefault(card.getAnimal(), "❓");
        System.out.println("updateCardButtonAppearance: animal=" + card.getAnimal() + ", emoji=" + emoji + ", isFlipped=" + card.isFlipped() + ", isMatched=" + card.isMatched());

        if (card.isMatched()) {
            button.setText(emoji);
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #10B981, #059669);"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 48px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-font-family: 'Segoe UI Emoji';"
                    + "-fx-background-radius: 12;"
                    + "-fx-opacity: 0.9;"
                    + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.6), 15, 0, 0, 5);"
            );
            button.setDisable(true); // Disable matched cards
        } else if (card.isFlipped()) {
            // Use graphic instead of text for better emoji rendering
            javafx.scene.control.Label emojiLabel = new javafx.scene.control.Label(emoji);
            emojiLabel.setStyle(
                    "-fx-font-size: 48px;"
                    + "-fx-font-family: 'Segoe UI Emoji';"
                    + "-fx-text-fill: black;"
            );
            button.setGraphic(emojiLabel);
            button.setText("");
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #FCD34D, #FBBF24);"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: #F59E0B;"
                    + "-fx-border-width: 3;"
                    + "-fx-border-radius: 12;"
                    + "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.5), 12, 0, 0, 4);"
            );
        } else {
            button.setGraphic(null);
            button.setText("?");
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #E879F9, #C084FC);"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 40;"
                    + "-fx-font-weight: bold;"
                    + "-fx-background-radius: 12;"
                    + "-fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
            );
        }
    }

    private void handleCardClick(Card card) {
        if (!isMyTurn()) {
            showInfo("Not Your Turn", "Please wait for your turn");
            return;
        }

        if (card.isFlipped() || card.isMatched()) {
            return;
        }

        // Send flip card message to server
        networkManager.flipCard(card.getId());
    }

    private void setupNetworkHandlers() {
        networkManager.setMessageHandler(message -> {
            handleNetworkMessage(message);
        });
    }

    private void handleNetworkMessage(Message message) {
        switch (message.getType()) {
            case CARD_FLIPPED:
                handleCardFlipped(message);
                break;
            case MATCH_FOUND:
                handleMatchFound(message);
                break;
            case NO_MATCH:
                handleNoMatch(message);
                break;
            case TURN_CHANGED:
                handleTurnChanged(message);
                break;
            case SCORE_UPDATE:
                handleScoreUpdate(message);
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
            default:
                break;
        }
    }

    // Network message handlers
    private void handleCardFlipped(Message message) {
        int cardId = message.getData().get("cardId").getAsInt();
        System.out.println("handleCardFlipped: cardId=" + cardId);

        javafx.application.Platform.runLater(() -> {
            for (Card card : gameState.getCards()) {
                if (card.getId() == cardId) {
                    System.out.println("Found card: id=" + card.getId() + ", animal=" + card.getAnimal() + ", isFlipped=" + card.isFlipped());
                    card.setFlipped(true);
                    Button button = cardButtonMap.get(cardId);
                    if (button != null) {
                        System.out.println("Updating button appearance for card: " + card.getAnimal());
                        updateCardButtonAppearance(button, card);
                        System.out.println("Button text after update: " + button.getText());
                    } else {
                        System.out.println("ERROR: Button not found for cardId " + cardId);
                    }
                    break;
                }
            }
        });
    }

    private void handleMatchFound(Message message) {
        int card1Id = message.getData().get("card1Id").getAsInt();
        int card2Id = message.getData().get("card2Id").getAsInt();

        javafx.application.Platform.runLater(() -> {
            for (Card card : gameState.getCards()) {
                if (card.getId() == card1Id || card.getId() == card2Id) {
                    card.setMatched(true);
                    Button button = cardButtonMap.get(card.getId());
                    if (button != null) {
                        updateCardButtonAppearance(button, card);
                    }
                }
            }
        });
    }

    private void handleNoMatch(Message message) {
        int card1Id = message.getData().get("card1Id").getAsInt();
        int card2Id = message.getData().get("card2Id").getAsInt();

        javafx.application.Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        for (Card card : gameState.getCards()) {
                            if (card.getId() == card1Id || card.getId() == card2Id) {
                                card.setFlipped(false);
                                Button button = cardButtonMap.get(card.getId());
                                if (button != null) {
                                    updateCardButtonAppearance(button, card);
                                }
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    private void handleTurnChanged(Message message) {
        String currentTurnPlayer = message.getData().get("currentTurn").getAsString();
        System.out.println("GameBoardController: Turn changed to " + currentTurnPlayer);
        javafx.application.Platform.runLater(() -> {
            // Convert player name to turn number
            int turnNumber = currentTurnPlayer.equals(gameState.getPlayer1Name()) ? 1 : 2;
            gameState.setCurrentTurn(turnNumber);
            updateTurnIndicator();
            System.out.println("GameBoardController: Turn indicator updated. Current turn: " + turnNumber + " (My turn: " + isMyTurn() + ")");
        });
    }

    private void handleScoreUpdate(Message message) {
        int player1Score = message.getData().get("player1Score").getAsInt();
        int player2Score = message.getData().get("player2Score").getAsInt();
        javafx.application.Platform.runLater(() -> {
            gameState.setPlayer1Score(player1Score);
            gameState.setPlayer2Score(player2Score);
            updateScores();
        });
    }

    private void handleGameOver(Message message) {
        String winner = message.getData().get("winner").getAsString();
        int player1Score = message.getData().get("player1Score").getAsInt();
        int player2Score = message.getData().get("player2Score").getAsInt();
        String currentPlayerName = networkManager.getCurrentPlayerName();

        javafx.application.Platform.runLater(() -> {
            String winnerText;
            if (winner.equals("tie")) {
                winnerText = "It's a Tie! 🤝";
            } else if (winner.equals(currentPlayerName)) {
                winnerText = "You Win! 🎉";
            } else {
                winnerText = winner + " Wins! 🎉";
            }
            turnIndicatorLabel.setText(winnerText);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(winnerText);
            alert.setContentText("Final Score:\n"
                    + gameState.getPlayer1Name() + ": " + player1Score + "\n"
                    + gameState.getPlayer2Name() + ": " + player2Score);
            alert.showAndWait();
        });
    }

    private void handlePlayerLeft(Message message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Player Left");
            alert.setHeaderText("Opponent Disconnected");
            alert.setContentText("The other player has left the game");
            alert.showAndWait();

            try {
                networkManager.disconnect();
                App.setRoot("home");
            } catch (IOException e) {
                showError("Navigation Error", "Failed to return to home");
            }
        });
    }

    private void handleError(Message message) {
        String errorMessage = message.getData().get("message").getAsString();
        javafx.application.Platform.runLater(() -> {
            showError("Error", errorMessage);
        });
    }

    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(gameState.getPlayer1Score()));
        player2ScoreLabel.setText(String.valueOf(gameState.getPlayer2Score()));
    }

    private void updateTurnIndicator() {
        int currentTurn = gameState.getCurrentTurn();
        boolean isPlayer1Turn = (currentTurn == 1);

        if (isPlayer1Turn) {
            turnIndicatorLabel.setText(gameState.getPlayer1Name() + "'s Turn");
            player1TurnLabel.setVisible(true);
            player2TurnLabel.setVisible(false);
            player1Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #10B981; -fx-border-width: 3; -fx-border-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.3), 15, 0, 0, 5);");
            player2Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;");
        } else {
            turnIndicatorLabel.setText(gameState.getPlayer2Name() + "'s Turn");
            player1TurnLabel.setVisible(false);
            player2TurnLabel.setVisible(true);
            player2Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #3B82F6; -fx-border-width: 3; -fx-border-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
            player1Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;");
        }
    }

    private boolean isMyTurn() {
        if (gameState == null) {
            return false;
        }
        String myName = networkManager.getCurrentPlayerName();
        int currentTurn = gameState.getCurrentTurn();
        if (currentTurn == 1) {
            return myName.equals(gameState.getPlayer1Name());
        } else {
            return myName.equals(gameState.getPlayer2Name());
        }
    }

    private int getGridSize() {
        String gridSizeStr = gameState.getGridSize();
        if (gridSizeStr.equals("4x4")) {
            return 4;
        }
        if (gridSizeStr.equals("6x6")) {
            return 6;
        }
        return 4;
    }

    private void setupButtonHover(Button button) {
        if (button == null) {
            return;
        }
        button.setOnMouseEntered(e -> button.setOpacity(0.7));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
    }

    @FXML
    private void onLeave() {
        if (networkManager.isConnected()) {
            networkManager.disconnect();
        }
        try {
            App.setRoot("home");
        } catch (IOException e) {
            showError("Navigation Error", "Failed to return to home");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
