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
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = 25;

        // Hover effect for unmatched cards
        if (isHovered && !card.isMatched()) {
            width -= 4;
            height -= 4;
            g2d.translate(2, 2);
        }

        // Card background
        if (isFlipped || card.isMatched()) {
            // When flipped, show animal against a colored radial gradient
            Color animalColor = getAnimalColor();
            Paint paint = new RadialGradientPaint(width / 2f, height / 2f, width,
                    new float[]{0f, 1f}, new Color[]{animalColor.brighter(), animalColor.darker()});
            g2d.setPaint(paint);
        } else {
            // Back of the card with a cute pattern
            g2d.setColor(UIUtils.PRIMARY_PINK);
        }
        g2d.fillRoundRect(0, 0, width, height, arc, arc);

        // Border
        if (card.isMatched()) {
            g2d.setColor(UIUtils.PRIMARY_GREEN); // Green glow for matched cards
            g2d.setStroke(new BasicStroke(4));
        } else {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
        }
        g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        // Apply flip animation opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Content
        if (isFlipped || card.isMatched()) {
            // Animal emoji takes center stage
            String emoji = UIUtils.getAnimalEmoji(card.getAnimal());
            Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, (int) (Math.min(width, height) * 0.6));
            g2d.setFont(emojiFont);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(emoji)) / 2;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(emoji, x, y);
        } else {
            // Cute icon for the back of the card
            String icon = "🐾"; // Paw print
            Font iconFont = new Font("Segoe UI Emoji", Font.PLAIN, (int) (Math.min(width, height) * 0.5));
            g2d.setFont(iconFont);
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(icon)) / 2;
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(icon, x, y);
        }

        g2d.dispose();
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
