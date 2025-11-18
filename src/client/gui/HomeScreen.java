package client.gui;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JPanel {
    private MainFrame mainFrame;

    public HomeScreen(MainFrame mainFrame) {
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

        // Title
        JLabel titleLabel = UIUtils.createTitleLabel("Memory Game", UIUtils.PRIMARY_PURPLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Subtitle
        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Match the animals and have fun!");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Create Room Button
        JButton createButton = UIUtils.createStyledButton("Create Room", UIUtils.PRIMARY_GREEN);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> mainFrame.showScreen("CREATE_ROOM"));
        cardPanel.add(createButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Join Room Button
        JButton joinButton = UIUtils.createStyledButton("Join Room", UIUtils.PRIMARY_BLUE);
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.addActionListener(e -> mainFrame.showScreen("JOIN_ROOM"));
        cardPanel.add(joinButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // How to Play Button
        JButton howToPlayButton = UIUtils.createOutlineButton("How to Play", UIUtils.PRIMARY_PURPLE);
        howToPlayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        howToPlayButton.addActionListener(e -> mainFrame.showScreen("HOW_TO_PLAY"));
        cardPanel.add(howToPlayButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Change Server Button
        JButton changeServerButton = UIUtils.createOutlineButton("⚙ Change Server IP", UIUtils.TEXT_LIGHT);
        changeServerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeServerButton.addActionListener(e -> changeServerIP());
        cardPanel.add(changeServerButton);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Server status
        String serverIP = mainFrame.getGameClient().getServerIP();
        JLabel serverLabel = UIUtils.createSubtitleLabel("📶 Connected to: " + serverIP);
        serverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverLabel.setForeground(UIUtils.PRIMARY_GREEN);
        cardPanel.add(serverLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // WiFi notice
        JLabel wifiLabel = UIUtils.createSubtitleLabel("Play with friends on the same network!");
        wifiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(wifiLabel);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void changeServerIP() {
        String currentIP = mainFrame.getGameClient().getServerIP();

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Enter Server IP Address:"));

        JTextField ipField = new JTextField(currentIP != null ? currentIP : "localhost");
        panel.add(ipField);

        panel.add(new JLabel("💡 Tip: Use ipconfig (Windows) or ifconfig (Mac/Linux)"));
        panel.add(new JLabel("on server to find IP address"));

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Server IP",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newIP = ipField.getText().trim();
            if (!newIP.isEmpty()) {
                // Disconnect from current server
                mainFrame.getGameClient().disconnect();

                // Try to connect to new server
                if (mainFrame.getGameClient().connect(newIP)) {
                    UIUtils.showInfo(this, "Successfully connected to " + newIP);
                    // Refresh screen to show new IP
                    mainFrame.showScreen("HOME");
                } else {
                    UIUtils.showError(this, "Failed to connect to " + newIP);
                    // Try to reconnect to old server
                    if (currentIP != null) {
                        mainFrame.getGameClient().connect(currentIP);
                    }
                }
            }
        }
    }
}
