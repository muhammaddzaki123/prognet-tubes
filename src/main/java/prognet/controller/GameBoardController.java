package prognet.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import prognet.App;
import prognet.common.Card;

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

    // Game state
    private String roomCode;
    private int gridSize = 4; // Default 4x4
    private int player1Score = 0;
    private int player2Score = 0;
    private int currentPlayer = 1;
    private List<Card> cards;
    private Button[][] cardButtons;
    private Card firstFlippedCard = null;
    private Card secondFlippedCard = null;
    private boolean canFlip = true;

    // Animal emojis for the cards
    private static final String[] ANIMALS = {
        "🦁", "🐯", "🐘", "🦒", "🦓", "🦏", "🐊", "🦜",
        "🐍", "🦘", "🦎", "🦢", "🦩", "🦫", "🦦", "🦥"
    };

    @FXML
    public void initialize() {
        setupButtonHover(leaveBtn);
        initializeGame();
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof String) {
            this.roomCode = (String) data;
            roomCodeLabel.setText(roomCode);
        }
    }

    private void initializeGame() {
        // Create cards
        createCards();

        // Setup grid
        setupGameBoard();

        // Update UI
        updateTurnIndicator();
    }

    private void createCards() {
        cards = new ArrayList<>();
        int totalCards = gridSize * gridSize;
        int pairs = totalCards / 2;

        // Create pairs of cards
        for (int i = 0; i < pairs; i++) {
            String animal = ANIMALS[i % ANIMALS.length];
            cards.add(new Card(i * 2, animal));
            cards.add(new Card(i * 2 + 1, animal));
        }

        // Shuffle cards
        Collections.shuffle(cards);
    }

    private void setupGameBoard() {
        cardGrid.getChildren().clear();
        cardButtons = new Button[gridSize][gridSize];

        int cardIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                final Card card = cards.get(cardIndex);
                final int currentIndex = cardIndex;

                // Create card button
                Button cardButton = new Button("?");
                cardButton.setPrefSize(100, 100);
                cardButton.setMaxSize(100, 100);
                cardButton.setMinSize(100, 100);
                cardButton.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #E879F9, #C084FC);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 40;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 12;"
                        + "-fx-cursor: hand;"
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
                );

                // Add hover effect
                cardButton.setOnMouseEntered(e -> {
                    if (!card.isFlipped() && !card.isMatched() && canFlip) {
                        cardButton.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #D946EF, #A855F7);"
                                + "-fx-text-fill: white;"
                                + "-fx-font-size: 40;"
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
                                + "-fx-font-size: 40;"
                                + "-fx-font-weight: bold;"
                                + "-fx-background-radius: 12;"
                                + "-fx-cursor: hand;"
                                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
                        );
                    }
                });

                // Add click handler
                cardButton.setOnAction(e -> handleCardClick(card, cardButton));

                cardButtons[row][col] = cardButton;
                cardGrid.add(cardButton, col, row);
                cardIndex++;
            }
        }
    }

    private void handleCardClick(Card card, Button button) {
        if (!canFlip || card.isFlipped() || card.isMatched()) {
            return;
        }

        // Flip the card
        card.setFlipped(true);
        button.setText(card.getAnimal());
        button.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #333333;"
                + "-fx-font-size: 40;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E879F9;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(232, 121, 249, 0.4), 12, 0, 0, 4);"
        );

        if (firstFlippedCard == null) {
            // First card flipped
            firstFlippedCard = card;
        } else if (secondFlippedCard == null) {
            // Second card flipped
            secondFlippedCard = card;
            canFlip = false;

            // Check for match after a short delay
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> checkMatch());
            pause.play();
        }
    }

    private void checkMatch() {
        if (firstFlippedCard.getAnimal().equals(secondFlippedCard.getAnimal())) {
            // Match found!
            firstFlippedCard.setMatched(true);
            secondFlippedCard.setMatched(true);

            // Update score
            if (currentPlayer == 1) {
                player1Score++;
                player1ScoreLabel.setText(String.valueOf(player1Score));
            } else {
                player2Score++;
                player2ScoreLabel.setText(String.valueOf(player2Score));
            }

            // Mark matched cards visually
            markCardAsMatched(firstFlippedCard);
            markCardAsMatched(secondFlippedCard);

            // Same player continues
        } else {
            // No match - flip cards back
            flipCardBack(firstFlippedCard);
            flipCardBack(secondFlippedCard);

            // Switch turns
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            updateTurnIndicator();
        }

        // Reset flipped cards
        firstFlippedCard = null;
        secondFlippedCard = null;
        canFlip = true;

        // Check if game is over
        checkGameOver();
    }

    private void markCardAsMatched(Card card) {
        Button button = findCardButton(card);
        if (button != null) {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #10B981, #059669);"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 40;"
                    + "-fx-background-radius: 12;"
                    + "-fx-opacity: 0.7;"
                    + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.4), 12, 0, 0, 4);"
            );
        }
    }

    private void flipCardBack(Card card) {
        Button button = findCardButton(card);
        if (button != null) {
            card.setFlipped(false);
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

    private Button findCardButton(Card card) {
        int index = cards.indexOf(card);
        int row = index / gridSize;
        int col = index % gridSize;
        return cardButtons[row][col];
    }

    private void updateTurnIndicator() {
        if (currentPlayer == 1) {
            turnIndicatorLabel.setText("Player 1's Turn");
            player1TurnLabel.setVisible(true);
            player2TurnLabel.setVisible(false);

            // Highlight player 1 card
            player1Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #10B981; -fx-border-width: 3; -fx-border-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.3), 15, 0, 0, 5);"
            );
            player2Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;"
            );
        } else {
            turnIndicatorLabel.setText("Player 2's Turn");
            player1TurnLabel.setVisible(false);
            player2TurnLabel.setVisible(true);
            player2TurnLabel.setText("Your turn!");

            // Highlight player 2 card
            player2Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #3B82F6; -fx-border-width: 3; -fx-border-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);"
            );
            player1Card.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20 25 20 25; "
                    + "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 15;"
            );
        }
    }

    private void checkGameOver() {
        boolean allMatched = cards.stream().allMatch(Card::isMatched);

        if (allMatched) {
            canFlip = false;

            // Determine winner
            String winnerText;
            if (player1Score > player2Score) {
                winnerText = "Player 1 Wins! 🎉";
            } else if (player2Score > player1Score) {
                winnerText = "Player 2 Wins! 🎉";
            } else {
                winnerText = "It's a Tie! 🤝";
            }

            turnIndicatorLabel.setText(winnerText);

            // TODO: Show game over dialog
            System.out.println("Game Over! " + winnerText);
            System.out.println("Final Score - Player 1: " + player1Score + ", Player 2: " + player2Score);
        }
    }

    private void setupButtonHover(Button button) {
        if (button == null) {
            return;
        }

        button.setOnMouseEntered(e -> {
            button.setOpacity(0.7);
        });

        button.setOnMouseExited(e -> {
            button.setOpacity(1.0);
        });
    }

    @FXML
    private void onLeave() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
        roomCodeLabel.setText(roomCode);
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
}
