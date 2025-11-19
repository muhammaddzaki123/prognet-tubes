package client.gui;

import common.GameState;
import javax.swing.*;
import java.awt.*;

public class GameResultScreen extends JPanel {
    private MainFrame mainFrame;
    private JLabel resultLabel;
    private JLabel player1InfoLabel;
    private JLabel player2InfoLabel;

    public GameResultScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = UIUtils.createGradientPanel();
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Card panel
        JPanel cardPanel = UIUtils.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        // Trophy Icon
        JLabel trophyLabel = new JLabel("🏆", SwingConstants.CENTER);
        trophyLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(trophyLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Result Title
        resultLabel = UIUtils.createTitleLabel("Game Over!", UIUtils.PRIMARY_PURPLE);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(resultLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Scores Panel
        JPanel scoresPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        scoresPanel.setOpaque(false);
        scoresPanel.setMaximumSize(new Dimension(500, 150));

        // Player 1 Score Card
        JPanel player1Card = new JPanel();
        player1Card.setLayout(new BoxLayout(player1Card, BoxLayout.Y_AXIS));
        player1Card.setOpaque(false);

        player1InfoLabel = new JLabel("<html><center>Player 1<br><b>0</b><br>matches</center></html>");
        player1InfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 18));
        player1InfoLabel.setForeground(UIUtils.TEXT_DARK);
        player1InfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player1Card.add(Box.createVerticalGlue());
        player1Card.add(player1InfoLabel);
        player1Card.add(Box.createVerticalGlue());

        scoresPanel.add(player1Card);

        // Player 2 Score Card
        JPanel player2Card = new JPanel();
        player2Card.setLayout(new BoxLayout(player2Card, BoxLayout.Y_AXIS));
        player2Card.setOpaque(false);

        player2InfoLabel = new JLabel("<html><center>Player 2<br><b>0</b><br>matches</center></html>");
        player2InfoLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 18));
        player2InfoLabel.setForeground(UIUtils.TEXT_DARK);
        player2InfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        player2Card.add(Box.createVerticalGlue());
        player2Card.add(player2InfoLabel);
        player2Card.add(Box.createVerticalGlue());

        scoresPanel.add(player2Card);

        cardPanel.add(scoresPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Play Again Button
        JButton playAgainButton = UIUtils.createStyledButton("Play Again", UIUtils.PRIMARY_GREEN);
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.addActionListener(e -> {
            mainFrame.getGameClient().disconnect();
            mainFrame.showScreen("HOME");
        });
        cardPanel.add(playAgainButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Return to Home Button
        JButton homeButton = UIUtils.createOutlineButton("Return to Home", UIUtils.PRIMARY_PURPLE);
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeButton.addActionListener(e -> {
            mainFrame.getGameClient().disconnect();
            mainFrame.showScreen("HOME");
        });
        cardPanel.add(homeButton);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showResult(GameState gameState, int playerNumber) {
        SwingUtilities.invokeLater(() -> {
            int player1Score = gameState.getPlayer1Score();
            int player2Score = gameState.getPlayer2Score();

            String result;
            Color resultColor;

            if (player1Score > player2Score) {
                result = playerNumber == 1 ? "You Win! 🎉" : "You Lose!";
                resultColor = playerNumber == 1 ? UIUtils.PRIMARY_GREEN : UIUtils.PRIMARY_BLUE;
            } else if (player2Score > player1Score) {
                result = playerNumber == 2 ? "You Win! 🎉" : "You Lose!";
                resultColor = playerNumber == 2 ? UIUtils.PRIMARY_GREEN : UIUtils.PRIMARY_BLUE;
            } else {
                result = "It's a Draw! 🤝";
                resultColor = UIUtils.PRIMARY_PURPLE;
            }

            resultLabel.setText(result);
            resultLabel.setForeground(resultColor);

            String player1Info = String.format(
                    "<html><center>%s<br><b style='font-size:36px'>%d</b><br>matches</center></html>",
                    gameState.getPlayer1Name(), player1Score);
            player1InfoLabel.setText(player1Info);

            String player2Info = String.format(
                    "<html><center>%s<br><b style='font-size:36px'>%d</b><br>matches</center></html>",
                    gameState.getPlayer2Name(), player2Score);
            player2InfoLabel.setText(player2Info);
        });
    }
}
