package client.gui;

import javax.swing.*;
import java.awt.*;

public class HowToPlayScreen extends JPanel {
    private MainFrame mainFrame;

    public HowToPlayScreen(MainFrame mainFrame) {
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
        JLabel titleLabel = UIUtils.createTitleLabel("How to Play", UIUtils.PRIMARY_PURPLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subtitleLabel = UIUtils.createSubtitleLabel("Learn the rules and start having fun!");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitleLabel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Steps
        String[][] steps = {
                { "1", "Connect with Friends",
                        "One player creates a room and shares the code. The other player joins using that code.",
                        "green" },
                { "2", "Find Matching Pairs",
                        "Take turns flipping two cards at a time. Try to find matching animal pairs!", "blue" },
                { "3", "Earn Points",
                        "When you find a match, you earn a point and get another turn. Wrong match? Next player's turn!",
                        "purple" },
                { "4", "Win the Game", "The player with the most matches when all cards are flipped wins the game!",
                        "pink" }
        };

        for (String[] step : steps) {
            JPanel stepPanel = createStepPanel(step[0], step[1], step[2], step[3]);
            stepPanel.setMaximumSize(new Dimension(500, 100));
            cardPanel.add(stepPanel);
            cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tips Panel
        JPanel tipsPanel = new JPanel();
        tipsPanel.setBackground(new Color(252, 231, 243));
        tipsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_PINK, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        tipsPanel.setMaximumSize(new Dimension(500, 150));
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));

        JLabel tipsTitle = new JLabel("💡 Pro Tips");
        tipsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tipsTitle.setForeground(UIUtils.PRIMARY_PINK);
        tipsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        tipsPanel.add(tipsTitle);

        tipsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] tips = {
                "• Pay attention to where the animals are located when they're revealed",
                "• Try to remember the positions of animals you've already seen",
                "• Both players need to be on the same WiFi network to play together",
                "• Have fun and enjoy playing with your friends!"
        };

        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            tipLabel.setForeground(UIUtils.TEXT_DARK);
            tipsPanel.add(tipLabel);
        }

        cardPanel.add(tipsPanel);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Got It Button
        JButton gotItButton = UIUtils.createStyledButton("Got It! Let's Play", UIUtils.PRIMARY_PURPLE);
        gotItButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gotItButton.addActionListener(e -> mainFrame.showScreen("HOME"));
        cardPanel.add(gotItButton);

        contentPanel.add(cardPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStepPanel(String number, String title, String description, String colorTheme) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));

        Color bgColor, borderColor, textColor;
        switch (colorTheme) {
            case "green":
                bgColor = new Color(220, 252, 231);
                borderColor = UIUtils.PRIMARY_GREEN;
                textColor = UIUtils.PRIMARY_GREEN;
                break;
            case "blue":
                bgColor = new Color(219, 234, 254);
                borderColor = UIUtils.PRIMARY_BLUE;
                textColor = UIUtils.PRIMARY_BLUE;
                break;
            case "purple":
                bgColor = new Color(243, 232, 255);
                borderColor = UIUtils.PRIMARY_PURPLE;
                textColor = UIUtils.PRIMARY_PURPLE;
                break;
            case "pink":
                bgColor = new Color(252, 231, 243);
                borderColor = UIUtils.PRIMARY_PINK;
                textColor = UIUtils.PRIMARY_PINK;
                break;
            default:
                bgColor = Color.WHITE;
                borderColor = Color.GRAY;
                textColor = Color.BLACK;
        }

        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Number badge
        JLabel numberLabel = new JLabel(number, SwingConstants.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 20));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(textColor);
        numberLabel.setPreferredSize(new Dimension(40, 40));
        panel.add(numberLabel, BorderLayout.WEST);

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(textColor);
        textPanel.add(titleLabel);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(UIUtils.TEXT_DARK);
        textPanel.add(descLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }
}
