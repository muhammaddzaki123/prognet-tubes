package client.gui.javafx;

import common.Card;
import common.GameState;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameBoardView extends BorderPane {
    private ClientApp clientApp;
    private GameState gameState;
    private Label player1NameLabel;
    private Label player1ScoreLabel;
    private Label player2NameLabel;
    private Label player2ScoreLabel;
    private Label turnLabel;
    private GridPane gridPanel;
    private List<MemoryCardNode> cardNodes;
    private int playerNumber;
    private int flippedCount = 0;

    // Chat
    private TextArea chatArea;
    private TextField messageField;
    private VBox chatPanel;
    private boolean chatVisible = false;

    public GameBoardView(ClientApp clientApp) {
        this.clientApp = clientApp;
        cardNodes = new ArrayList<>();
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        // Top Header
        HBox headerPanel = new HBox(20);
        headerPanel.setPadding(new javafx.geometry.Insets(10));
        headerPanel.setAlignment(Pos.CENTER_LEFT);

        Button leaveButton = new Button("← Leave Game");
        leaveButton.setOnAction(e -> {
            if (UIUtilsFX.showConfirm("Are you sure you want to leave the game?")) {
                clientApp.getGameClient().disconnect();
                clientApp.getMainView().showView("HOME");
            }
        });

        Button chatToggleButton = new Button("💬 Chat");
        chatToggleButton.setOnAction(e -> toggleChat());

        headerPanel.getChildren().addAll(leaveButton, new HBox(20), chatToggleButton);
        setTop(headerPanel);

        // Center Game Area
        VBox centerPanel = new VBox(20);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new javafx.geometry.Insets(10));

        // Scoreboard
        HBox scoreboardPanel = new HBox(50);
        scoreboardPanel.setAlignment(Pos.CENTER);

        VBox p1Box = new VBox(5);
        p1Box.setAlignment(Pos.CENTER);
        player1NameLabel = new Label("Player 1");
        player1NameLabel.setFont(UIUtilsFX.BUTTON_FONT);
        player1ScoreLabel = new Label("0");
        player1ScoreLabel.setFont(UIUtilsFX.TITLE_FONT);
        player1ScoreLabel.setTextFill(UIUtilsFX.PRIMARY_GREEN);
        p1Box.getChildren().addAll(player1NameLabel, player1ScoreLabel);

        VBox p2Box = new VBox(5);
        p2Box.setAlignment(Pos.CENTER);
        player2NameLabel = new Label("Player 2");
        player2NameLabel.setFont(UIUtilsFX.BUTTON_FONT);
        player2ScoreLabel = new Label("0");
        player2ScoreLabel.setFont(UIUtilsFX.TITLE_FONT);
        player2ScoreLabel.setTextFill(UIUtilsFX.PRIMARY_BLUE);
        p2Box.getChildren().addAll(player2NameLabel, player2ScoreLabel);

        scoreboardPanel.getChildren().addAll(p1Box, p2Box);
        centerPanel.getChildren().add(scoreboardPanel);

        // Turn Indicator
        turnLabel = new Label("Your Turn!");
        turnLabel.setFont(Font.font("Arial Rounded MT Bold", 24));
        centerPanel.getChildren().add(turnLabel);

        // Grid
        gridPanel = new GridPane();
        gridPanel.setAlignment(Pos.CENTER);
        gridPanel.setHgap(10);
        gridPanel.setVgap(10);
        centerPanel.getChildren().add(gridPanel);

        setCenter(centerPanel);

        // Chat Panel (Right Side)
        chatPanel = new VBox(10);
        chatPanel.setPrefWidth(250);
        chatPanel.setPadding(new javafx.geometry.Insets(10));
        chatPanel.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
        chatPanel.setVisible(false);
        chatPanel.setManaged(false); // Don't take space when hidden

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);

        HBox messageBox = new HBox(5);
        messageField = new TextField();
        messageField.setPromptText("Type a message...");
        messageField.setOnAction(e -> sendMessage());
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());
        messageBox.getChildren().addAll(messageField, sendButton);

        chatPanel.getChildren().addAll(new Label("Chat"), chatArea, messageBox);

        setRight(chatPanel);
    }

    private void toggleChat() {
        chatVisible = !chatVisible;
        chatPanel.setVisible(chatVisible);
        chatPanel.setManaged(chatVisible);
    }

    public void initializeGame(GameState gameState, int playerNumber) {
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.flippedCount = 0;

        player1NameLabel.setText(gameState.getPlayer1Name());
        player2NameLabel.setText(gameState.getPlayer2Name());
        updateScores();
        updateTurn();

        int gridSize = Integer.parseInt(gameState.getGridSize().split("x")[0]);
        gridPanel.getChildren().clear();
        cardNodes.clear();

        List<Card> cards = gameState.getCards();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            MemoryCardNode cardNode = new MemoryCardNode(card, i);
            cardNode.setOnMouseClicked(e -> handleCardClick(cardNode));
            cardNodes.add(cardNode);
            gridPanel.add(cardNode, i % gridSize, i / gridSize);
        }
    }

    private void handleCardClick(MemoryCardNode cardNode) {
        if (cardNode.getCard().isMatched() || cardNode.getCard().isFlipped()) return;
        if (gameState.getCurrentTurn() != playerNumber) {
            // Ideally show a small toast/tooltip
            return;
        }
        if (flippedCount >= 2) return;

        clientApp.getGameClient().flipCard(cardNode.getCard().getId());
    }

    public void handleCardFlipped(int cardId) {
        if (cardId >= 0 && cardId < cardNodes.size()) {
            MemoryCardNode node = cardNodes.get(cardId);
            node.flip(true);
            flippedCount++;
        }
    }

    public void handleMatchResult(boolean isMatch, GameState newGameState) {
        this.gameState = newGameState;

        // Update all cards
        for (int i = 0; i < newGameState.getCards().size(); i++) {
            cardNodes.get(i).updateCard(newGameState.getCards().get(i));
        }

        flippedCount = 0;
        updateScores();
        updateTurn();

        if (gameState.isGameOver()) {
             // Handled by GAME_OVER message
        }
    }

    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(gameState.getPlayer1Score()));
        player2ScoreLabel.setText(String.valueOf(gameState.getPlayer2Score()));
    }

    private void updateTurn() {
        if (gameState.getCurrentTurn() == playerNumber) {
            turnLabel.setText("🎲 Your Turn!");
            turnLabel.setTextFill(UIUtilsFX.PRIMARY_GREEN);
        } else {
            turnLabel.setText("⏳ Opponent's Turn");
            turnLabel.setTextFill(UIUtilsFX.TEXT_LIGHT);
        }
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            clientApp.getGameClient().sendChatMessage(msg);
            messageField.clear();
        }
    }

    public void addChatMessage(String sender, String message) {
        chatArea.appendText(String.format("[%s]: %s\n", sender, message));
        if (!chatVisible) {
            // Maybe show a notification indicator?
        }
    }
}

class MemoryCardNode extends StackPane {
    private Card card;
    private int index;
    private Rectangle bg;
    private Label content;
    private boolean isFlipped = false;

    public MemoryCardNode(Card card, int index) {
        this.card = card;
        this.index = index;

        setPrefSize(80, 80);
        setCursor(Cursor.HAND);

        bg = new Rectangle(80, 80);
        bg.setArcWidth(20);
        bg.setArcHeight(20);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);

        content = new Label();
        content.setFont(Font.font("Segoe UI Emoji", 40));

        getChildren().addAll(bg, content);

        updateVisuals(card.isFlipped() || card.isMatched());
    }

    public Card getCard() { return card; }

    public void flip(boolean flipped) {
        if (isFlipped == flipped) return;
        isFlipped = flipped;

        // Animation
        FadeTransition ft = new FadeTransition(Duration.millis(200), this);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            updateVisuals(flipped);
            FadeTransition ft2 = new FadeTransition(Duration.millis(200), this);
            ft2.setFromValue(0.0);
            ft2.setToValue(1.0);
            ft2.play();
        });
        ft.play();
    }

    public void updateCard(Card newCard) {
        this.card = newCard;
        // Update state immediately if needed (e.g. match found)
        updateVisuals(card.isFlipped() || card.isMatched());
    }

    private void updateVisuals(boolean showFace) {
        if (showFace) {
            bg.setFill(UIUtilsFX.getAnimalColor(card.getAnimal()));
            content.setText(UIUtilsFX.getAnimalEmoji(card.getAnimal()));
            if (card.isMatched()) {
                bg.setStroke(UIUtilsFX.PRIMARY_GREEN);
                bg.setStrokeWidth(4);
                setEffect(new DropShadow(10, UIUtilsFX.PRIMARY_GREEN));
            } else {
                bg.setStroke(Color.WHITE);
                setEffect(null);
            }
        } else {
            bg.setFill(UIUtilsFX.PRIMARY_PINK);
            content.setText("🐾");
            content.setTextFill(Color.WHITE);
            bg.setStroke(Color.WHITE);
            setEffect(null);
        }
    }
}
