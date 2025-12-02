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
                                + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
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
                                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);");
            }
        });

        // Add click handler
        cardButton.setOnAction(e -> handleCardClick(card));

        return cardButton;
    }

    private void updateCardButtonAppearance(Button button, Card card) {
        String emoji = animalEmojis.getOrDefault(card.getAnimal(), "❓");
        System.out.println("updateCardButtonAppearance: animal=" + card.getAnimal() + ", emoji=" + emoji
                + ", isFlipped=" + card.isFlipped() + ", isMatched=" + card.isMatched());

        if (card.isMatched()) {
            // Use graphic instead of text for better emoji rendering
            javafx.scene.control.Label emojiLabel = new javafx.scene.control.Label(emoji);
            emojiLabel.setStyle(
                    "-fx-font-size: 48px;"
                            + "-fx-font-family: 'Segoe UI Emoji';"
                            + "-fx-text-fill: black;");
            button.setGraphic(emojiLabel);
            button.setText("");
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #10B981, #059669);"
                            + "-fx-background-radius: 12;"
                            + "-fx-opacity: 0.9;"
                            + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.6), 15, 0, 0, 5);");
            button.setDisable(true); // Disable matched cards
        } else if (card.isFlipped()) {
            // Use graphic instead of text for better emoji rendering
            javafx.scene.control.Label emojiLabel = new javafx.scene.control.Label(emoji);
            emojiLabel.setStyle(
                    "-fx-font-size: 48px;"
                            + "-fx-font-family: 'Segoe UI Emoji';"
                            + "-fx-text-fill: black;");
            button.setGraphic(emojiLabel);
            button.setText("");
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #FCD34D, #FBBF24);"
                            + "-fx-background-radius: 12;"
                            + "-fx-border-color: #F59E0B;"
                            + "-fx-border-width: 3;"
                            + "-fx-border-radius: 12;"
                            + "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.5), 12, 0, 0, 4);");
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
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);");
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
            case GAME_STARTED:
                handleGameStarted(message);
                break;
            case PLAYER_LEFT:
                handlePlayerLeft(message);
                break;
            case ERROR:
                handleError(message);
                break;
            case REMATCH_VOTE_UPDATE:
                handleRematchVoteUpdate(message);
                break;
            case REMATCH_ACCEPTED:
                handleRematchAccepted(message);
                break;
            case REMATCH_DECLINED:
                handleRematchDeclined(message);
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
                    System.out.println("Found card: id=" + card.getId() + ", animal=" + card.getAnimal()
                            + ", isFlipped=" + card.isFlipped());
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
            System.out.println("GameBoardController: Turn indicator updated. Current turn: " + turnNumber
                    + " (My turn: " + isMyTurn() + ")");
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
        int player1TotalWins = message.getData().has("player1TotalWins")
                ? message.getData().get("player1TotalWins").getAsInt()
                : 0;
        int player2TotalWins = message.getData().has("player2TotalWins")
                ? message.getData().get("player2TotalWins").getAsInt()
                : 0;
        String currentPlayerName = networkManager.getCurrentPlayerName();
        String player1Name = gameState.getPlayer1Name();
        String player2Name = gameState.getPlayer2Name();

        javafx.application.Platform.runLater(() -> {
            // Update final scores
            gameState.setPlayer1Score(player1Score);
            gameState.setPlayer2Score(player2Score);
            gameState.setPlayer1TotalWins(player1TotalWins);
            gameState.setPlayer2TotalWins(player2TotalWins);
            updateScores();

            // Determine result for each player
            String player1Result;
            String player2Result;
            String player1ResultStyle;
            String player2ResultStyle;
            String winnerText;
            String headerStyle;
            Alert.AlertType alertType;

            if (winner.equals("tie")) {
                // SERI
                player1Result = "SERI 🤝";
                player2Result = "SERI 🤝";
                player1ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;";
                player2ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;";
                winnerText = "Permainan Seri! 🤝";
                headerStyle = "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;";
                alertType = Alert.AlertType.INFORMATION;
                turnIndicatorLabel.setText("🤝 SERI 🤝");
                turnIndicatorLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");

                // Style both cards equally for tie
                player1Card.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #FEF3C7, #FDE68A);"
                                + "-fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                + "-fx-border-color: #F59E0B; -fx-border-width: 3; -fx-border-radius: 15;"
                                + "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.5), 20, 0, 0, 5);");
                player2Card.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #FEF3C7, #FDE68A);"
                                + "-fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                + "-fx-border-color: #F59E0B; -fx-border-width: 3; -fx-border-radius: 15;"
                                + "-fx-effect: dropshadow(gaussian, rgba(245, 158, 11, 0.5), 20, 0, 0, 5);");
            } else {
                // Ada pemenang
                if (winner.equals(player1Name)) {
                    player1Result = "MENANG 🏆";
                    player2Result = "KALAH 😔";
                    player1ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10B981;";
                    player2ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #EF4444;";

                    // Style winner card (player 1)
                    player1Card.setStyle(
                            "-fx-background-color: linear-gradient(to bottom right, #D1FAE5, #A7F3D0);"
                                    + "-fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                    + "-fx-border-color: #10B981; -fx-border-width: 4; -fx-border-radius: 15;"
                                    + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.6), 25, 0, 0, 8);");
                    // Style loser card (player 2)
                    player2Card.setStyle(
                            "-fx-background-color: #F3F4F6; -fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                    + "-fx-border-color: #9CA3AF; -fx-border-width: 2; -fx-border-radius: 15;"
                                    + "-fx-opacity: 0.7;");
                } else {
                    player1Result = "KALAH 😔";
                    player2Result = "MENANG 🏆";
                    player1ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #EF4444;";
                    player2ResultStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10B981;";

                    // Style loser card (player 1)
                    player1Card.setStyle(
                            "-fx-background-color: #F3F4F6; -fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                    + "-fx-border-color: #9CA3AF; -fx-border-width: 2; -fx-border-radius: 15;"
                                    + "-fx-opacity: 0.7;");
                    // Style winner card (player 2)
                    player2Card.setStyle(
                            "-fx-background-color: linear-gradient(to bottom right, #DBEAFE, #BFDBFE);"
                                    + "-fx-background-radius: 15; -fx-padding: 20 25 20 25;"
                                    + "-fx-border-color: #3B82F6; -fx-border-width: 4; -fx-border-radius: 15;"
                                    + "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.6), 25, 0, 0, 8);");
                }

                // Set header text based on current player perspective
                if (winner.equals(currentPlayerName)) {
                    winnerText = "🎉 ANDA MENANG! 🎉";
                    headerStyle = "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #10B981;";
                    alertType = Alert.AlertType.INFORMATION;
                    turnIndicatorLabel.setText("🏆 ANDA MENANG! 🏆");
                    turnIndicatorLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
                } else {
                    winnerText = "😔 ANDA KALAH 😔";
                    headerStyle = "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #EF4444;";
                    alertType = Alert.AlertType.WARNING;
                    turnIndicatorLabel.setText("💔 ANDA KALAH 💔");
                    turnIndicatorLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
                }
            }

            // Update player subtitles with result
            player1SubtitleLabel.setText(player1Result);
            player1SubtitleLabel.setStyle(player1ResultStyle);
            player2SubtitleLabel.setText(player2Result);
            player2SubtitleLabel.setStyle(player2ResultStyle);

            // Hide turn indicators
            player1TurnLabel.setVisible(false);
            player2TurnLabel.setVisible(false);

            // Create custom styled alert
            Alert alert = new Alert(alertType);
            alert.setTitle("🎮 Permainan Selesai");
            alert.setHeaderText(null);

            // Create custom content with detailed results
            Label headerLabel = new Label(winnerText);
            headerLabel.setStyle(headerStyle);

            // Detailed score breakdown
            Label scoreHeaderLabel = new Label("\n📊 Hasil Akhir:");
            scoreHeaderLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #374151;");

            // Player 1 result
            Label player1ResultLabel = new Label(
                    "\n🎯 " + player1Name + ": " + player1Score + " poin (Round)");
            player1ResultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #1F2937;");

            Label player1StatusLabel = new Label("   Status: " + player1Result);
            player1StatusLabel.setStyle(player1ResultStyle);

            Label player1TotalLabel = new Label("   🏆 Total Kemenangan: " + player1TotalWins);
            player1TotalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");

            // Player 2 result
            Label player2ResultLabel = new Label(
                    "\n🎯 " + player2Name + ": " + player2Score + " poin (Round)");
            player2ResultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #1F2937;");

            Label player2StatusLabel = new Label("   Status: " + player2Result);
            player2StatusLabel.setStyle(player2ResultStyle);

            Label player2TotalLabel = new Label("   🏆 Total Kemenangan: " + player2TotalWins);
            player2TotalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");

            // Result summary
            Label summaryLabel = new Label("\n" + (winner.equals("tie")
                    ? "Kedua pemain memiliki skor yang sama!"
                    : winner + " memenangkan permainan!"));
            summaryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-font-style: italic;");

            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(5);
            content.getChildren().addAll(
                    headerLabel,
                    scoreHeaderLabel,
                    player1ResultLabel,
                    player1StatusLabel,
                    player1TotalLabel,
                    player2ResultLabel,
                    player2StatusLabel,
                    player2TotalLabel,
                    summaryLabel);
            content.setStyle("-fx-padding: 25; -fx-alignment: center;");

            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setMinWidth(500);
            alert.getDialogPane().setMinHeight(400);

            // Customize button text with rematch and home options
            javafx.scene.control.ButtonType rematchButton = new javafx.scene.control.ButtonType(
                    "🔄 Main Lagi",
                    javafx.scene.control.ButtonBar.ButtonData.YES);
            javafx.scene.control.ButtonType homeButton = new javafx.scene.control.ButtonType(
                    "🏠 Kembali ke Menu",
                    javafx.scene.control.ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(rematchButton, homeButton);

            // Show alert and handle response
            alert.showAndWait().ifPresent(response -> {
                if (response == rematchButton) {
                    // Player wants rematch
                    networkManager.voteRematch(true);
                    showRematchWaitingDialog();
                } else {
                    // Player wants to go home
                    networkManager.leaveToHome();
                    try {
                        networkManager.disconnect();
                        App.setRoot("home");
                    } catch (IOException e) {
                        showError("Navigation Error", "Gagal kembali ke menu utama");
                    }
                }
            });
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

    private void handleGameStarted(Message message) {
        javafx.application.Platform.runLater(() -> {
            // Close rematch waiting dialog if open
            closeRematchWaitingDialog();

            // Extract game state from message
            com.google.gson.JsonObject gameStateJson = message.getData().get("gameState").getAsJsonObject();

            // Update game state with new data
            gameState.setGameOver(false);
            gameState.setGameStarted(true);
            gameState.setCurrentTurn(gameStateJson.get("currentTurn").getAsInt());

            // Parse cards
            com.google.gson.JsonArray cardsArray = gameStateJson.get("cards").getAsJsonArray();
            java.util.List<Card> newCards = new java.util.ArrayList<>();
            for (int i = 0; i < cardsArray.size(); i++) {
                com.google.gson.JsonObject cardObj = cardsArray.get(i).getAsJsonObject();
                Card card = new Card(
                        cardObj.get("id").getAsInt(),
                        cardObj.get("animal").getAsString());
                card.setFlipped(cardObj.get("flipped").getAsBoolean());
                card.setMatched(cardObj.get("matched").getAsBoolean());
                newCards.add(card);
            }
            gameState.setCards(newCards);

            // Reset UI state
            player1TurnLabel.setVisible(false);
            player2TurnLabel.setVisible(false);

            // Reset player card styles to normal
            player1Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                            + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;");
            player2Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                            + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;");

            // Reset subtitle labels based on player perspective
            String myName = networkManager.getCurrentPlayerName();
            if (myName.equals(gameState.getPlayer1Name())) {
                player1SubtitleLabel.setText("You");
                player1SubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
                player2SubtitleLabel.setText("Opponent");
                player2SubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
            } else {
                player1SubtitleLabel.setText("Opponent");
                player1SubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
                player2SubtitleLabel.setText("You");
                player2SubtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
            }

            // Rebuild game board with new cards
            setupGameBoard();

            // Update scores and turn indicator
            updateScores();
            updateTurnIndicator();

            System.out.println("Game restarted after rematch!");
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
            networkManager.leaveToHome();
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

    // Rematch dialog and waiting indicator
    private Alert rematchWaitingAlert;

    private void showRematchWaitingDialog() {
        rematchWaitingAlert = new Alert(Alert.AlertType.INFORMATION);
        rematchWaitingAlert.setTitle("⏳ Menunggu Lawan");
        rematchWaitingAlert.setHeaderText(null);

        Label waitingLabel = new Label(
                "Menunggu keputusan lawan...\n\nAnda sudah memilih untuk main lagi.\nSilakan tunggu hingga lawan Anda membuat keputusan.");
        waitingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-padding: 20;");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().add(waitingLabel);
        content.setStyle("-fx-alignment: center; -fx-padding: 20;");

        rematchWaitingAlert.getDialogPane().setContent(content);
        rematchWaitingAlert.getDialogPane().setMinWidth(400);
        rematchWaitingAlert.getDialogPane().setMinHeight(200);

        // Remove default buttons
        rematchWaitingAlert.getButtonTypes().clear();

        // Add cancel button
        javafx.scene.control.ButtonType cancelButton = new javafx.scene.control.ButtonType(
                "Batalkan",
                javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        rematchWaitingAlert.getButtonTypes().add(cancelButton);

        // Handle cancel in a separate thread to avoid blocking
        new Thread(() -> {
            rematchWaitingAlert.showAndWait().ifPresent(response -> {
                if (response == cancelButton) {
                    javafx.application.Platform.runLater(() -> {
                        networkManager.leaveToHome();
                        try {
                            networkManager.disconnect();
                            App.setRoot("home");
                        } catch (IOException ex) {
                            showError("Navigation Error", "Gagal kembali ke menu utama");
                        }
                    });
                }
            });
        }).start();
    }

    private void closeRematchWaitingDialog() {
        if (rematchWaitingAlert != null && rematchWaitingAlert.isShowing()) {
            rematchWaitingAlert.close();
            rematchWaitingAlert = null;
        }
    }

    private void handleRematchVoteUpdate(Message message) {
        boolean player1Voted = message.getData().get("player1WantsRematch").getAsBoolean();
        boolean player2Voted = message.getData().get("player2WantsRematch").getAsBoolean();

        javafx.application.Platform.runLater(() -> {
            gameState.setPlayer1WantsRematch(player1Voted);
            gameState.setPlayer2WantsRematch(player2Voted);

            // Update waiting dialog if shown
            if (rematchWaitingAlert != null && rematchWaitingAlert.isShowing()) {
                String myName = networkManager.getCurrentPlayerName();
                String opponentName = myName.equals(gameState.getPlayer1Name())
                        ? gameState.getPlayer2Name()
                        : gameState.getPlayer1Name();

                Label updatedLabel = new Label(
                        "Menunggu keputusan " + opponentName + "...\n\n" +
                                "Anda sudah memilih untuk main lagi.\n" +
                                "Silakan tunggu hingga lawan Anda membuat keputusan.");
                updatedLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151; -fx-padding: 20;");

                javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
                content.getChildren().add(updatedLabel);
                content.setStyle("-fx-alignment: center; -fx-padding: 20;");

                rematchWaitingAlert.getDialogPane().setContent(content);
            }
        });
    }

    private void handleRematchAccepted(Message message) {
        javafx.application.Platform.runLater(() -> {
            closeRematchWaitingDialog();

            // Show brief message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎮 Main Lagi");
            alert.setHeaderText(null);
            alert.setContentText("Kedua pemain setuju untuk main lagi!\nGame baru akan dimulai...");
            alert.show();

            // Auto close after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            // Game will restart automatically via GAME_STARTED message from server
        });
    }

    private void handleRematchDeclined(Message message) {
        String reason = message.getData().has("reason") ? message.getData().get("reason").getAsString()
                : "Salah satu pemain menolak";

        javafx.application.Platform.runLater(() -> {
            closeRematchWaitingDialog();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("❌ Tidak Main Lagi");
            alert.setHeaderText(null);
            alert.setContentText(reason + "\n\nKembali ke menu utama...");

            javafx.scene.control.ButtonType okButton = new javafx.scene.control.ButtonType(
                    "OK",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            alert.showAndWait();

            try {
                networkManager.disconnect();
                App.setRoot("home");
            } catch (IOException e) {
                showError("Navigation Error", "Gagal kembali ke menu utama");
            }
        });
    }
}
