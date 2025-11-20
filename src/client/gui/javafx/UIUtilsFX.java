package client.gui.javafx;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Optional;

public class UIUtilsFX {
    // Fun & Cute Color Palette
    public static final Color BG_GRADIENT_START = Color.rgb(255, 240, 245); // Light Pink
    public static final Color BG_GRADIENT_END = Color.rgb(230, 240, 255); // Light Blue
    public static final Color PRIMARY_GREEN = Color.rgb(29, 185, 84);   // Spotify Green
    public static final Color PRIMARY_BLUE = Color.rgb(0, 122, 255);    // Apple Blue
    public static final Color PRIMARY_PURPLE = Color.rgb(109, 40, 217); // Deep Purple
    public static final Color PRIMARY_PINK = Color.rgb(255, 20, 147);   // Deep Pink
    public static final Color CARD_BG = Color.rgb(255, 255, 255, 0.8); // Semi-Transparent White
    public static final Color TEXT_DARK = Color.rgb(40, 40, 40);
    public static final Color TEXT_LIGHT = Color.rgb(100, 100, 100);

    public static final Font TITLE_FONT = Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 48);
    public static final Font BUTTON_FONT = Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 16);
    public static final Font NORMAL_FONT = Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 14);
    public static final Font INPUT_FONT = Font.font("Arial Rounded MT Bold", FontWeight.NORMAL, 18);

    public static Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY)));
        button.setCursor(Cursor.HAND);
        button.setPrefSize(300, 50);

        // Hover effect
        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(bgColor.darker(), new CornerRadii(10), Insets.EMPTY))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY))));

        return button;
    }

    public static Button createOutlineButton(String text, Color borderColor) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setTextFill(borderColor);
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        button.setCursor(Cursor.HAND);
        button.setPrefSize(300, 50);

        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(borderColor.deriveColor(0, 1, 1, 0.1), new CornerRadii(10), Insets.EMPTY))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY))));

        return button;
    }

    public static Background createGradientBackground() {
        Stop[] stops = new Stop[] { new Stop(0, BG_GRADIENT_START), new Stop(1, BG_GRADIENT_END) };
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        return new Background(new BackgroundFill(lg, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static VBox createCardPanel() {
        VBox panel = new VBox();
        panel.setBackground(new Background(new BackgroundFill(CARD_BG, new CornerRadii(20), Insets.EMPTY)));
        panel.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(4))));
        panel.setPadding(new Insets(30));
        panel.setMaxWidth(400); // Limit width for card look
        panel.setAlignment(javafx.geometry.Pos.CENTER);

        // Shadow effect via style (JavaFX effect would be DropShadow)
        panel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        return panel;
    }

    public static Label createTitleLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(TITLE_FONT);
        label.setTextFill(color);
        return label;
    }

    public static Label createSubtitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(NORMAL_FONT);
        label.setTextFill(TEXT_LIGHT);
        return label;
    }

    public static TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setFont(INPUT_FONT);
        field.setPrefSize(300, 40);
        field.setMaxWidth(300);
        // Style via CSS is cleaner, but we do inline for simplicity here
        field.setStyle("-fx-background-color: white; -fx-border-color: #007aff; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 10 5 10;");
        return field;
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Get Unicode emoji for animal names
     */
    public static String getAnimalEmoji(String animal) {
        switch (animal.toLowerCase()) {
            case "tiger": return "🐯";
            case "sloth": return "🦥";
            case "toucan": return "🦜";
            case "orangutan": return "🦧";
            case "lemur": return "🐒";
            case "rhino": return "🦏";
            case "crocodile": return "🐊";
            case "redpanda": return "🐼";
            case "warthog": return "🐗";
            case "antelope": return "🦌";
            default: return "🦊";
        }
    }

    public static Color getAnimalColor(String animal) {
        switch (animal.toLowerCase()) {
            case "tiger": return Color.rgb(255, 140, 0);
            case "sloth": return Color.rgb(139, 90, 43);
            case "toucan": return Color.rgb(255, 215, 0);
            case "orangutan": return Color.rgb(205, 92, 92);
            case "lemur": return Color.rgb(169, 169, 169);
            case "rhino": return Color.rgb(128, 128, 128);
            case "crocodile": return Color.rgb(34, 139, 34);
            case "redpanda": return Color.rgb(178, 34, 34);
            case "warthog": return Color.rgb(160, 82, 45);
            case "antelope": return Color.rgb(210, 180, 140);
            default: return Color.rgb(100, 100, 100);
        }
    }
}
