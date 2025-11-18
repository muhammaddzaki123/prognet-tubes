package client.gui;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    // Color palette
    public static final Color BG_GRADIENT_START = new Color(220, 252, 231);
    public static final Color BG_GRADIENT_END = new Color(237, 233, 254);
    public static final Color PRIMARY_GREEN = new Color(74, 222, 128);
    public static final Color PRIMARY_BLUE = new Color(96, 165, 250);
    public static final Color PRIMARY_PURPLE = new Color(168, 85, 247);
    public static final Color PRIMARY_PINK = new Color(244, 114, 182);
    public static final Color CARD_BG = new Color(255, 255, 255, 230);
    public static final Color TEXT_DARK = new Color(55, 65, 81);
    public static final Color TEXT_LIGHT = new Color(107, 114, 128);

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static JButton createOutlineButton(String text, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setForeground(borderColor);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(
                        new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, BG_GRADIENT_START, w, h, BG_GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 4),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        return panel;
    }

    public static JLabel createTitleLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        label.setForeground(color);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT_LIGHT);
        return label;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 18));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Get Unicode emoji for animal names
     */
    public static String getAnimalEmoji(String animal) {
        switch (animal.toLowerCase()) {
            case "tiger":
                return "🐯";
            case "sloth":
                return "🦥";
            case "toucan":
                return "🦜";
            case "orangutan":
                return "🦧";
            case "lemur":
                return "🐒";
            case "rhino":
                return "🦏";
            case "crocodile":
                return "🐊";
            case "redpanda":
                return "🐼";
            case "warthog":
                return "🐗";
            case "antelope":
                return "🦌";
            default:
                return "🦊";
        }
    }

    /**
     * Get icon character for UI elements
     */
    public static String getIconChar(String iconName) {
        switch (iconName.toLowerCase()) {
            case "play":
                return "▶";
            case "trophy":
                return "🏆";
            case "star":
                return "⭐";
            case "heart":
                return "❤";
            case "home":
                return "🏠";
            case "settings":
                return "⚙";
            case "user":
                return "👤";
            case "users":
                return "👥";
            case "check":
                return "✓";
            case "cross":
                return "✕";
            case "info":
                return "ℹ";
            case "question":
                return "❓";
            case "sparkle":
                return "✨";
            case "fire":
                return "🔥";
            case "brain":
                return "🧠";
            case "game":
                return "🎮";
            case "wifi":
                return "📶";
            default:
                return "●";
        }
    }
}
