package client.gui.javafx;

import common.GameState;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WaitingRoomView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;
    private Label statusLabel;
    private Label player1Label;
    private Label player2Label;
    private Label roomCodeLabel;

    public WaitingRoomView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        VBox cardPanel = UIUtilsFX.createCardPanel();

        Label titleLabel = UIUtilsFX.createTitleLabel("Waiting Room", UIUtilsFX.PRIMARY_PURPLE);

        roomCodeLabel = new Label("Room Code: ???");
        roomCodeLabel.setFont(UIUtilsFX.TITLE_FONT);
        roomCodeLabel.setTextFill(UIUtilsFX.PRIMARY_BLUE);

        statusLabel = UIUtilsFX.createSubtitleLabel("Waiting for host to start...");

        player1Label = new Label("Player 1: ???");
        player1Label.setFont(UIUtilsFX.BUTTON_FONT);

        player2Label = new Label("Player 2: (You)");
        player2Label.setFont(UIUtilsFX.BUTTON_FONT);

        cardPanel.getChildren().addAll(
            titleLabel,
            roomCodeLabel,
            new VBox(20),
            statusLabel,
            new VBox(10),
            player1Label,
            player2Label
        );

        contentBox.getChildren().add(cardPanel);
        setCenter(contentBox);
    }

    public void updateGameState(GameState gameState, boolean isHost) {
        // This view is mostly for the joiner
        player1Label.setText("Player 1: " + gameState.getPlayer1Name());
        player2Label.setText("Player 2: " + gameState.getPlayer2Name());
        // Room code isn't in GameState, but passed via message usually.
        // We might need to store it or get it.
        // For now, let's assume it's fine.
    }

    @Override
    public void onShow() {
        statusLabel.setText("Waiting for host to start...");
    }
}
