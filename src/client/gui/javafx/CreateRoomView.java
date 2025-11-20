package client.gui.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CreateRoomView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;
    private TextField nameField;
    private ComboBox<String> gridSizeBox;
    private ComboBox<String> themeBox;
    private VBox settingsPanel;
    private VBox waitingPanel;
    private Label roomCodeLabel;
    private Label statusLabel;

    public CreateRoomView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        // Settings Panel
        settingsPanel = UIUtilsFX.createCardPanel();

        Label titleLabel = UIUtilsFX.createTitleLabel("Create Room", UIUtilsFX.PRIMARY_GREEN);

        nameField = UIUtilsFX.createStyledTextField("Enter your name");

        gridSizeBox = new ComboBox<>();
        gridSizeBox.getItems().addAll("4x4", "6x6");
        gridSizeBox.setValue("4x4");
        gridSizeBox.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px; -fx-pref-width: 300px;");

        themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Animals", "Coming Soon...");
        themeBox.setValue("Animals");
        themeBox.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 16px; -fx-pref-width: 300px;");

        Button createButton = UIUtilsFX.createStyledButton("Create & Wait", UIUtilsFX.PRIMARY_GREEN);
        createButton.setOnAction(e -> createRoom());

        Button backButton = UIUtilsFX.createOutlineButton("Back", UIUtilsFX.TEXT_LIGHT);
        backButton.setOnAction(e -> clientApp.getMainView().showView("HOME"));

        settingsPanel.getChildren().addAll(
            titleLabel,
            new Label("Your Name:"), nameField,
            new Label("Grid Size:"), gridSizeBox,
            new Label("Theme:"), themeBox,
            new VBox(15),
            createButton,
            new VBox(5),
            backButton
        );

        // Waiting Panel (Initially hidden)
        waitingPanel = UIUtilsFX.createCardPanel();
        waitingPanel.setVisible(false);

        Label waitingTitle = UIUtilsFX.createTitleLabel("Waiting...", UIUtilsFX.PRIMARY_PURPLE);

        roomCodeLabel = new Label("Room Code: ???");
        roomCodeLabel.setFont(UIUtilsFX.TITLE_FONT);
        roomCodeLabel.setTextFill(UIUtilsFX.PRIMARY_BLUE);

        statusLabel = UIUtilsFX.createSubtitleLabel("Waiting for player 2...");

        Button cancelButton = UIUtilsFX.createOutlineButton("Cancel", Color.RED);
        cancelButton.setOnAction(e -> cancelRoom());

        waitingPanel.getChildren().addAll(
            waitingTitle,
            roomCodeLabel,
            new VBox(20),
            statusLabel,
            new VBox(20),
            cancelButton
        );

        contentBox.getChildren().addAll(settingsPanel, waitingPanel);
        // Since both are in VBox, we need to manage visibility properly.
        // Actually better to just swap content or add/remove.
        // Let's use StackPane approach for contentBox, or just rebuild/hide.
        // For simplicity, I will just hide waitingPanel and not add it to VBox initially.
        contentBox.getChildren().clear();
        contentBox.getChildren().add(settingsPanel);

        setCenter(contentBox);
    }

    private void createRoom() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            UIUtilsFX.showError("Please enter your name");
            return;
        }

        clientApp.getGameClient().createRoom(name, gridSizeBox.getValue(), themeBox.getValue());

        // Show waiting panel
        VBox contentBox = (VBox) getCenter();
        contentBox.getChildren().clear();
        contentBox.getChildren().add(waitingPanel);
        waitingPanel.setVisible(true);
        settingsPanel.setVisible(false);
    }

    private void cancelRoom() {
        clientApp.getGameClient().disconnect();
        clientApp.getGameClient().autoConnect(); // Reconnect to clean state
        resetView();
    }

    public void setRoomCode(String code) {
        roomCodeLabel.setText("Room Code: " + code);
    }

    public void setPlayer2Joined(String player2Name) {
        statusLabel.setText(player2Name + " joined! Starting...");
        // Game start will be handled by GAME_STARTED message
    }

    @Override
    public void onShow() {
        resetView();
    }

    private void resetView() {
        if (getCenter() instanceof VBox) {
            VBox contentBox = (VBox) getCenter();
            contentBox.getChildren().clear();
            contentBox.getChildren().add(settingsPanel);
        }
        settingsPanel.setVisible(true);
        waitingPanel.setVisible(false);
        statusLabel.setText("Waiting for player 2...");
    }
}
