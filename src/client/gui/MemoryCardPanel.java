package client.gui;

import common.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MemoryCardPanel extends JPanel {
    private Card card;
    private boolean isFlipped;
    private boolean isHovered;
    private BufferedImage animalImage;
    private float opacity = 1.0f;
    private Timer flipTimer;

    public MemoryCardPanel(Card card) {
        this.card = card;
        this.isFlipped = false;
        this.isHovered = false;

        setPreferredSize(new Dimension(80, 80));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Load animal image (simplified - using text for now)
        loadAnimalImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!card.isMatched() && !isFlipped) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    private void loadAnimalImage() {
        // Using emoji - no need to load external PNG files
        // Emoji will be rendered directly in paintComponent
        animalImage = null; // Not needed anymore
    }

    private Color getAnimalColor() {
        // Different colors for different animals
        switch (card.getAnimal()) {
            case "tiger":
                return new Color(255, 140, 0);
            case "sloth":
                return new Color(139, 90, 43);
            case "toucan":
                return new Color(255, 215, 0);
            case "orangutan":
                return new Color(205, 92, 92);
            case "lemur":
                return new Color(169, 169, 169);
            case "rhino":
                return new Color(128, 128, 128);
            case "crocodile":
                return new Color(34, 139, 34);
            case "redpanda":
                return new Color(178, 34, 34);
            case "warthog":
                return new Color(160, 82, 45);
            case "antelope":
                return new Color(210, 180, 140);
            default:
                return new Color(100, 100, 100);
        }
    }

    public void flip(boolean flipped) {
        if (this.isFlipped == flipped)
            return;

        this.isFlipped = flipped;

        // Animate flip
        if (flipTimer != null)
            flipTimer.stop();

        flipTimer = new Timer(10, e -> {
            opacity -= 0.1f;
            if (opacity <= 0) {
                opacity = 0;
                ((Timer) e.getSource()).stop();

                Timer showTimer = new Timer(10, e2 -> {
                    opacity += 0.1f;
                    if (opacity >= 1.0f) {
                        opacity = 1.0f;
                        ((Timer) e2.getSource()).stop();
                    }
                    repaint();
                });
                showTimer.start();
            }
            repaint();
        });
        flipTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Apply hover scale effect
        if (isHovered && !card.isMatched()) {
            g2d.scale(1.05, 1.05);
            width = (int) (width / 1.05);
            height = (int) (height / 1.05);
        }

        // Draw card background
        if (isFlipped || card.isMatched()) {
            // Front side (showing animal)
            if (card.isMatched()) {
                g2d.setColor(new Color(254, 240, 138)); // Yellow for matched
            } else {
                g2d.setColor(new Color(220, 252, 231)); // Green for flipped
            }
        } else {
            // Back side (hidden)
            g2d.setColor(new Color(192, 132, 252)); // Purple
        }

        g2d.fillRoundRect(0, 0, width, height, 15, 15);

        // Draw border
        g2d.setStroke(new BasicStroke(3));
        if (card.isMatched()) {
            g2d.setColor(new Color(250, 204, 21)); // Yellow border for matched
        } else {
            g2d.setColor(Color.WHITE);
        }
        g2d.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);

        // Draw content with opacity
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        if (isFlipped || card.isMatched()) {
            // Draw animal emoji
            String emoji = UIUtils.getAnimalEmoji(card.getAnimal());
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); // Support emoji rendering
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(emoji)) / 2;
            int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(emoji, x, y);
        } else {
            // Draw question mark
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "?";
            int x = (width - fm.stringWidth(text)) / 2;
            int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(text, x, y);
        }

        g2d.setComposite(originalComposite);

        // Draw sparkle effect for matched cards
        if (card.isMatched()) {
            g2d.setColor(new Color(250, 204, 21));
            int sparkleSize = 4;
            g2d.fillOval(width / 4, 5, sparkleSize, sparkleSize);
            g2d.fillOval(width * 3 / 4, height - 10, sparkleSize, sparkleSize);
            g2d.fillOval(width - 10, height / 2, sparkleSize, sparkleSize);
        }
    }

    public Card getCard() {
        return card;
    }

    public void updateCard(Card card) {
        this.card = card;
        this.isFlipped = card.isFlipped() || card.isMatched();
        repaint();
    }
}
