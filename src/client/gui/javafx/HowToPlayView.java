package client.gui.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class HowToPlayView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;

    public HowToPlayView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        VBox cardPanel = UIUtilsFX.createCardPanel();

        Label titleLabel = UIUtilsFX.createTitleLabel("How to Play", UIUtilsFX.PRIMARY_PURPLE);

        Label instructions = new Label(
            "1. Create a room or join a friend's room.\n" +
            "2. Wait for the other player to join.\n" +
            "3. Take turns flipping two cards.\n" +
            "4. If the cards match, you get a point and another turn!\n" +
            "5. If they don't match, it's the other player's turn.\n" +
            "6. The player with the most pairs wins! 🏆"
        );
        instructions.setFont(UIUtilsFX.NORMAL_FONT);
        instructions.setWrapText(true);
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setMaxWidth(350);

        Button backButton = UIUtilsFX.createStyledButton("Got it!", UIUtilsFX.PRIMARY_GREEN);
        backButton.setOnAction(e -> clientApp.getMainView().showView("HOME"));

        cardPanel.getChildren().addAll(
            titleLabel,
            new VBox(20),
            instructions,
            new VBox(30),
            backButton
        );

        contentBox.getChildren().add(cardPanel);
        setCenter(contentBox);
    }

    @Override
    public void onShow() {
        // nothing to refresh
    }
}
