package client.gui;

import javax.swing.*;
import java.awt.*;

public class CreateRoomScreen extends JPanel {
    private MainFrame mainFrame;
    private JLabel roomCodeLabel;
    private JLabel player2StatusLabel;
    private JButton startButton;
    private JComboBox<String> gridSizeCombo;
    private JComboBox<String> themeCombo;
    private String playerName;
    private boolean player2Joined = false;

    public CreateRoomScreen(MainFrame mainFrame) {
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
        JLabel titleLabel = UIUtils.createTitleLabel("Create Room", UIUtils.PRIMARY_GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Player Name Input
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(nameLabel);

        JTextField nameField = UIUtils.createStyledTextField();
        nameField.setText("Player 1");
        nameField.setMaximumSize(new Dimension(300, 40));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(nameField);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(2, 2, 10, 10));
        settingsPanel.setOpaque(false);
        settingsPanel.setMaximumSize(new Dimension(300, 100));

        settingsPanel.add(new JLabel("Grid Size:"));
        gridSizeCombo = new JComboBox<>(new String[] { "3x3", "4x4", "5x5" });
        gridSizeCombo.setSelectedIndex(1);
        settingsPanel.add(gridSizeCombo);

        settingsPanel.add(new JLabel("Theme:"));
        themeCombo = new JComboBox<>(new String[] { "jungle", "forest", "savanna", "ocean" });
        settingsPanel.add(themeCombo);

        cardPanel.add(settingsPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Room Code Display (initially hidden)
        JPanel roomCodePanel = new JPanel();
        roomCodePanel.setLayout(new BoxLayout(roomCodePanel, BoxLayout.Y_AXIS));
        roomCodePanel.setOpaque(false);
        roomCodePanel.setVisible(false);

        JLabel roomCodeTitleLabel = new JLabel("Room Code:");
        roomCodeTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roomCodePanel.add(roomCodeTitleLabel);

        roomCodeLabel = new JLabel("------");
        roomCodeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        roomCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roomCodePanel.add(roomCodeLabel);

        JButton copyButton = new JButton("Copy Code");
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.addActionListener(e -> {
            java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(
                    roomCodeLabel.getText());
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            UIUtils.showInfo(this, "Room code copied to clipboard!");
        });
        roomCodePanel.add(copyButton);

        cardPanel.add(roomCodePanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Player Status
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);
        playersPanel.setMaximumSize(new Dimension(300, 120));
        playersPanel.setVisible(false);

        JLabel player1Label = new JLabel("✓ Player 1 (You): Ready");
        player1Label.setForeground(UIUtils.PRIMARY_GREEN);
        playersPanel.add(player1Label);

        playersPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        player2StatusLabel = new JLabel("⏳ Waiting for Player 2...");
        player2StatusLabel.setForeground(UIUtils.TEXT_LIGHT);
        playersPanel.add(player2StatusLabel);

        cardPanel.add(playersPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create Room Button
        JButton createButton = UIUtils.createStyledButton("Create Room", UIUtils.PRIMARY_GREEN);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> {
            playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                UIUtils.showError(this, "Please enter your name");
                return;
            }

            String gridSize = (String) gridSizeCombo.getSelectedItem();
            String theme = (String) themeCombo.getSelectedItem();

            mainFrame.getGameClient().createRoom(playerName, gridSize, theme);

            createButton.setVisible(false);
            nameField.setEnabled(false);
            gridSizeCombo.setEnabled(false);
            themeCombo.setEnabled(false);
            roomCodePanel.setVisible(true);
            playersPanel.setVisible(true);
        });
        cardPanel.add(createButton);

        // Start Game Button (initially disabled)
        startButton = UIUtils.createStyledButton("Start Game", UIUtils.PRIMARY_PURPLE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setEnabled(false);
        startButton.setVisible(false);
        startButton.addActionListener(e -> {
            mainFrame.getGameClient().startGame();
        });
        cardPanel.add(startButton);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setRoomCode(String roomCode) {
        SwingUtilities.invokeLater(() -> {
            roomCodeLabel.setText(roomCode);
        });
    }

    public void setPlayer2Joined(String player2Name) {
        SwingUtilities.invokeLater(() -> {
            player2Joined = true;
            player2StatusLabel.setText("✓ " + player2Name + ": Connected");
            player2StatusLabel.setForeground(UIUtils.PRIMARY_BLUE);
            startButton.setEnabled(true);
            startButton.setVisible(true);
        });
    }
}
