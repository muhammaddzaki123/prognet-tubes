package client.gui.javafx;

import common.GameState;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameResultView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;
    private Label resultLabel;
    private Label scoreLabel;
    private Label messageLabel;

    public GameResultView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        VBox cardPanel = UIUtilsFX.createCardPanel();

        resultLabel = UIUtilsFX.createTitleLabel("Game Over", UIUtilsFX.PRIMARY_PURPLE);

        scoreLabel = UIUtilsFX.createSubtitleLabel("Score: 0 - 0");
        scoreLabel.setFont(UIUtilsFX.TITLE_FONT);

        messageLabel = new Label();
        messageLabel.setFont(UIUtilsFX.BUTTON_FONT);

        Button homeButton = UIUtilsFX.createStyledButton("Back to Home", UIUtilsFX.PRIMARY_BLUE);
        homeButton.setOnAction(e -> clientApp.getMainView().showView("HOME"));

        cardPanel.getChildren().addAll(
            resultLabel,
            scoreLabel,
            new VBox(20),
            messageLabel,
            new VBox(30),
            homeButton
        );

        contentBox.getChildren().add(cardPanel);
        setCenter(contentBox);
    }

    public void showResult(GameState gameState, int playerNumber) {
        int myScore = (playerNumber == 1) ? gameState.getPlayer1Score() : gameState.getPlayer2Score();
        int oppScore = (playerNumber == 1) ? gameState.getPlayer2Score() : gameState.getPlayer1Score();

        scoreLabel.setText(myScore + " - " + oppScore);

        if (myScore > oppScore) {
            resultLabel.setText("🏆 You Won! 🏆");
            resultLabel.setTextFill(UIUtilsFX.PRIMARY_GREEN);
            messageLabel.setText("Great memory! You found more matches.");
        } else if (myScore < oppScore) {
            resultLabel.setText("Better Luck Next Time");
            resultLabel.setTextFill(UIUtilsFX.PRIMARY_PURPLE);
            messageLabel.setText("Your opponent found more matches.");
        } else {
            resultLabel.setText("It's a Tie!");
            resultLabel.setTextFill(UIUtilsFX.PRIMARY_BLUE);
            messageLabel.setText("Both players have excellent memory!");
        }
    }

    @Override
    public void onShow() {
        // Result is set via showResult
    }
}
