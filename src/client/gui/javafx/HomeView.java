package client.gui.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class HomeView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;
    private Label serverLabel;

    public HomeView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        VBox cardPanel = UIUtilsFX.createCardPanel();

        Label titleLabel = UIUtilsFX.createTitleLabel("Memory Game", UIUtilsFX.PRIMARY_PURPLE);
        Label subtitleLabel = UIUtilsFX.createSubtitleLabel("Match the animals and have fun!");

        Button createButton = UIUtilsFX.createStyledButton("Create Room", UIUtilsFX.PRIMARY_GREEN);
        createButton.setOnAction(e -> clientApp.getMainView().showView("CREATE_ROOM"));

        Button joinButton = UIUtilsFX.createStyledButton("Join Room", UIUtilsFX.PRIMARY_BLUE);
        joinButton.setOnAction(e -> clientApp.getMainView().showView("JOIN_ROOM"));

        Button howToPlayButton = UIUtilsFX.createOutlineButton("How to Play", UIUtilsFX.PRIMARY_PURPLE);
        howToPlayButton.setOnAction(e -> clientApp.getMainView().showView("HOW_TO_PLAY"));

        Button changeServerButton = UIUtilsFX.createOutlineButton("⚙ Change Server IP", UIUtilsFX.TEXT_LIGHT);
        changeServerButton.setOnAction(e -> changeServerIP());

        serverLabel = UIUtilsFX.createSubtitleLabel("");
        serverLabel.setTextFill(UIUtilsFX.PRIMARY_GREEN);

        Label wifiLabel = UIUtilsFX.createSubtitleLabel("Play with friends on the same network!");

        cardPanel.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new VBox(15), // Spacer
            createButton,
            new VBox(5), // Spacer
            joinButton,
            new VBox(5), // Spacer
            howToPlayButton,
            new VBox(5), // Spacer
            changeServerButton,
            new VBox(10), // Spacer
            serverLabel,
            wifiLabel
        );

        // Add spacing between buttons
        VBox.setMargin(createButton, new javafx.geometry.Insets(10, 0, 0, 0));
        VBox.setMargin(joinButton, new javafx.geometry.Insets(10, 0, 0, 0));
        VBox.setMargin(howToPlayButton, new javafx.geometry.Insets(10, 0, 0, 0));
        VBox.setMargin(changeServerButton, new javafx.geometry.Insets(10, 0, 0, 0));
        VBox.setMargin(serverLabel, new javafx.geometry.Insets(15, 0, 0, 0));

        contentBox.getChildren().add(cardPanel);
        setCenter(contentBox);
    }

    @Override
    public void onShow() {
        String serverIP = clientApp.getGameClient().getServerIP();
        serverLabel.setText("📶 Connected to: " + (serverIP != null ? serverIP : "Not Connected"));
    }

    private void changeServerIP() {
        String currentIP = clientApp.getGameClient().getServerIP();

        TextInputDialog dialog = new TextInputDialog(currentIP != null ? currentIP : "localhost");
        dialog.setTitle("Change Server IP");
        dialog.setHeaderText("Enter Server IP Address:");
        dialog.setContentText("IP:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newIP = result.get().trim();
            if (!newIP.isEmpty()) {
                clientApp.getGameClient().disconnect();
                if (clientApp.getGameClient().connect(newIP)) {
                    UIUtilsFX.showInfo("Successfully connected to " + newIP);
                    onShow(); // Refresh label
                } else {
                    UIUtilsFX.showError("Failed to connect to " + newIP);
                    if (currentIP != null) {
                        clientApp.getGameClient().connect(currentIP);
                    }
                }
            }
        }
    }
}
