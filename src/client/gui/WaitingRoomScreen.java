package client.gui;

import common.GameState;
import javax.swing.*;
import java.awt.*;

public class WaitingRoomScreen extends JPanel {
    private MainFrame mainFrame;
    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel settingsLabel;
    private JButton startButton;
    private boolean isHost;

    public WaitingRoomScreen(MainFrame mainFrame) {
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

        // Back Button
        JButton backButton = new JButton("← Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.showScreen("HOME"));
        cardPanel.add(backButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        JLabel titleLabel = UIUtils.createTitleLabel("Waiting Room", UIUtils.PRIMARY_PURPLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Players Panel
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);
        playersPanel.setMaximumSize(new Dimension(400, 150));

        player1Label = new JLabel("🎮 Player 1: Connecting...");
        player1Label.setFont(new Font("Arial", Font.BOLD, 16));
        player1Label.setForeground(UIUtils.PRIMARY_GREEN);
        playersPanel.add(player1Label);

        playersPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        player2Label = new JLabel("🎯 Player 2: Connecting...");
        player2Label.setFont(new Font("Arial", Font.BOLD, 16));
        player2Label.setForeground(UIUtils.PRIMARY_BLUE);
        playersPanel.add(player2Label);

        cardPanel.add(playersPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(new Color(243, 232, 255));
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_PURPLE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        settingsPanel.setMaximumSize(new Dimension(400, 80));

        settingsLabel = new JLabel("<html><center><b>Game Settings</b><br>Loading...</center></html>");
        settingsLabel.setForeground(UIUtils.PRIMARY_PURPLE);
        settingsPanel.add(settingsLabel);

        cardPanel.add(settingsPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Start Button (for host only)
        startButton = UIUtils.createStyledButton("Start Game", UIUtils.PRIMARY_PURPLE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setVisible(false);
        startButton.addActionListener(e -> mainFrame.getGameClient().startGame());
        cardPanel.add(startButton);

        // Waiting message (for guest)
        JLabel waitingLabel = new JLabel("⏳ Waiting for host to start...");
        waitingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingLabel.setForeground(UIUtils.TEXT_LIGHT);
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(waitingLabel);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void updateGameState(GameState gameState, boolean isHost) {
        SwingUtilities.invokeLater(() -> {
            this.isHost = isHost;

            player1Label.setText("🎮 " + gameState.getPlayer1Name() + " (Host) - Ready");
            player2Label.setText("🎯 " + gameState.getPlayer2Name() + " - Ready");

            String settings = String.format(
                    "<html><center><b>Game Settings</b><br>Grid: %s | Theme: %s</center></html>",
                    gameState.getGridSize(),
                    gameState.getTheme());
            settingsLabel.setText(settings);

            if (isHost) {
                startButton.setVisible(true);
            }
        });
    }
}
