package client.gui;

import javax.swing.*;
import java.awt.*;

public class JoinRoomScreen extends JPanel {
    private MainFrame mainFrame;
    private JTextField roomCodeField;
    private JTextField nameField;

    public JoinRoomScreen(MainFrame mainFrame) {
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
        JLabel titleLabel = UIUtils.createTitleLabel("Join Room", UIUtils.PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Player Name Input
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(nameLabel);

        nameField = UIUtils.createStyledTextField();
        nameField.setText("Player 2");
        nameField.setMaximumSize(new Dimension(300, 40));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(nameField);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Room Code Input
        JLabel codeLabel = new JLabel("Enter Room Code:");
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(codeLabel);

        roomCodeField = UIUtils.createStyledTextField();
        roomCodeField.setMaximumSize(new Dimension(300, 40));
        roomCodeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(roomCodeField);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Join Button
        JButton joinButton = UIUtils.createStyledButton("Join Room", UIUtils.PRIMARY_BLUE);
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.addActionListener(e -> handleJoinRoom());
        cardPanel.add(joinButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Help text
        JPanel helpPanel = new JPanel();
        helpPanel.setBackground(new Color(219, 234, 254));
        helpPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_BLUE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        helpPanel.setMaximumSize(new Dimension(300, 80));

        JLabel helpLabel = new JLabel(
                "<html><center>💡 Ask your friend for the 6-digit room code to join their game!</center></html>");
        helpLabel.setForeground(UIUtils.PRIMARY_BLUE);
        helpPanel.add(helpLabel);

        cardPanel.add(helpPanel);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleJoinRoom() {
        String playerName = nameField.getText().trim();
        String roomCode = roomCodeField.getText().trim();

        if (playerName.isEmpty()) {
            UIUtils.showError(this, "Please enter your name");
            return;
        }

        if (roomCode.isEmpty() || roomCode.length() != 6) {
            UIUtils.showError(this, "Please enter a valid 6-digit room code");
            return;
        }

        mainFrame.getGameClient().joinRoom(playerName, roomCode);
    }

    public void reset() {
        roomCodeField.setText("");
    }
}
