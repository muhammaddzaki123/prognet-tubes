package client.gui;

import common.Card;
import common.GameState;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameBoardScreen extends JPanel {
    private MainFrame mainFrame;
    private GameState gameState;
    private JLabel player1NameLabel;
    private JLabel player1ScoreLabel;
    private JLabel player2NameLabel;
    private JLabel player2ScoreLabel;
    private JLabel turnLabel;
    private JPanel gridPanel;
    private List<MemoryCardPanel> cardPanels;
    private int playerNumber;
    private int flippedCount = 0;

    public GameBoardScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cardPanels = new ArrayList<>();
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = UIUtils.createGradientPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton leaveButton = new JButton("← Leave Game");
        leaveButton.addActionListener(e -> {
            if (UIUtils.showConfirm(this, "Are you sure you want to leave the game?")) {
                mainFrame.showScreen("HOME");
            }
        });
        headerPanel.add(leaveButton, BorderLayout.WEST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel - Scoreboard + Game Grid
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Scoreboard
        JPanel scoreboardPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        scoreboardPanel.setOpaque(false);
        scoreboardPanel.setPreferredSize(new Dimension(0, 100));

        // Player 1 Score
        JPanel player1Panel = new JPanel();
        player1Panel.setLayout(new BoxLayout(player1Panel, BoxLayout.Y_AXIS));
        player1Panel.setBackground(new Color(220, 252, 231));
        player1Panel.setBorder(BorderFactory.createLineBorder(UIUtils.PRIMARY_GREEN, 3));

        player1NameLabel = new JLabel("Player 1");
        player1NameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player1NameLabel.setForeground(UIUtils.PRIMARY_GREEN);
        player1NameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player1Panel.add(player1NameLabel);

        player1ScoreLabel = new JLabel("0");
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        player1ScoreLabel.setForeground(UIUtils.PRIMARY_GREEN);
        player1ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player1Panel.add(player1ScoreLabel);

        scoreboardPanel.add(player1Panel);

        // Player 2 Score
        JPanel player2Panel = new JPanel();
        player2Panel.setLayout(new BoxLayout(player2Panel, BoxLayout.Y_AXIS));
        player2Panel.setBackground(new Color(219, 234, 254));
        player2Panel.setBorder(BorderFactory.createLineBorder(UIUtils.PRIMARY_BLUE, 3));

        player2NameLabel = new JLabel("Player 2");
        player2NameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player2NameLabel.setForeground(UIUtils.PRIMARY_BLUE);
        player2NameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player2Panel.add(player2NameLabel);

        player2ScoreLabel = new JLabel("0");
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        player2ScoreLabel.setForeground(UIUtils.PRIMARY_BLUE);
        player2ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player2Panel.add(player2ScoreLabel);

        scoreboardPanel.add(player2Panel);

        centerPanel.add(scoreboardPanel, BorderLayout.NORTH);

        // Turn Indicator
        turnLabel = new JLabel("Your Turn!", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        turnLabel.setForeground(UIUtils.PRIMARY_PURPLE);
        turnLabel.setOpaque(true);
        turnLabel.setBackground(Color.WHITE);
        turnLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_PURPLE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        turnPanel.setOpaque(false);
        turnPanel.add(turnLabel);
        centerPanel.add(turnPanel, BorderLayout.CENTER);

        // Game Grid
        gridPanel = new JPanel();
        gridPanel.setOpaque(false);
        centerPanel.add(gridPanel, BorderLayout.SOUTH);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void initializeGame(GameState gameState, int playerNumber) {
        SwingUtilities.invokeLater(() -> {
            this.gameState = gameState;
            this.playerNumber = playerNumber;
            this.flippedCount = 0;

            player1NameLabel.setText(gameState.getPlayer1Name());
            player2NameLabel.setText(gameState.getPlayer2Name());
            updateScores();
            updateTurn();

            // Setup grid
            int gridSize = Integer.parseInt(gameState.getGridSize().split("x")[0]);
            gridPanel.removeAll();
            gridPanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));

            cardPanels.clear();
            for (Card card : gameState.getCards()) {
                MemoryCardPanel cardPanel = new MemoryCardPanel(card);
                cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        handleCardClick(cardPanel);
                    }
                });
                cardPanels.add(cardPanel);
                gridPanel.add(cardPanel);
            }

            gridPanel.revalidate();
            gridPanel.repaint();
        });
    }

    private void handleCardClick(MemoryCardPanel cardPanel) {
        Card card = cardPanel.getCard();

        if (card.isMatched() || card.isFlipped()) {
            return;
        }

        if (gameState.getCurrentTurn() != playerNumber) {
            UIUtils.showInfo(this, "It's not your turn!");
            return;
        }

        if (flippedCount >= 2) {
            return;
        }

        mainFrame.getGameClient().flipCard(card.getId());
    }

    public void handleCardFlipped(int cardId) {
        SwingUtilities.invokeLater(() -> {
            if (cardId >= 0 && cardId < cardPanels.size()) {
                MemoryCardPanel cardPanel = cardPanels.get(cardId);
                cardPanel.flip(true);
                flippedCount++;
            }
        });
    }

    public void handleMatchResult(boolean isMatch, GameState newGameState) {
        SwingUtilities.invokeLater(() -> {
            this.gameState = newGameState;

            // Update all cards
            for (int i = 0; i < newGameState.getCards().size(); i++) {
                cardPanels.get(i).updateCard(newGameState.getCards().get(i));
            }

            flippedCount = 0;
            updateScores();
            updateTurn();

            if (gameState.isGameOver()) {
                Timer timer = new Timer(1000, e -> {
                    mainFrame.showGameResult(gameState, playerNumber);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(gameState.getPlayer1Score()));
        player2ScoreLabel.setText(String.valueOf(gameState.getPlayer2Score()));
    }

    private void updateTurn() {
        if (gameState.getCurrentTurn() == playerNumber) {
            turnLabel.setText("🎲 Your Turn!");
            turnLabel.setForeground(UIUtils.PRIMARY_GREEN);
        } else {
            turnLabel.setText("⏳ Opponent's Turn");
            turnLabel.setForeground(UIUtils.TEXT_LIGHT);
        }
    }
}
