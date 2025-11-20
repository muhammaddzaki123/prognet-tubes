package client.gui.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class JoinRoomView extends BorderPane implements MainView.Refreshable {
    private ClientApp clientApp;
    private TextField nameField;
    private TextField roomCodeField;

    public JoinRoomView(ClientApp clientApp) {
        this.clientApp = clientApp;
        setBackground(UIUtilsFX.createGradientBackground());
        initComponents();
    }

    private void initComponents() {
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new javafx.geometry.Insets(20));

        VBox cardPanel = UIUtilsFX.createCardPanel();

        Label titleLabel = UIUtilsFX.createTitleLabel("Join Room", UIUtilsFX.PRIMARY_BLUE);

        nameField = UIUtilsFX.createStyledTextField("Enter your name");
        roomCodeField = UIUtilsFX.createStyledTextField("Enter 4-digit Room Code");

        Button joinButton = UIUtilsFX.createStyledButton("Join Game", UIUtilsFX.PRIMARY_BLUE);
        joinButton.setOnAction(e -> joinRoom());

        Button backButton = UIUtilsFX.createOutlineButton("Back", UIUtilsFX.TEXT_LIGHT);
        backButton.setOnAction(e -> clientApp.getMainView().showView("HOME"));

        cardPanel.getChildren().addAll(
            titleLabel,
            new Label("Your Name:"), nameField,
            new Label("Room Code:"), roomCodeField,
            new VBox(15),
            joinButton,
            new VBox(5),
            backButton
        );

        contentBox.getChildren().add(cardPanel);
        setCenter(contentBox);
    }

    private void joinRoom() {
        String name = nameField.getText().trim();
        String code = roomCodeField.getText().trim();

        if (name.isEmpty() || code.isEmpty()) {
            UIUtilsFX.showError("Please enter both name and room code");
            return;
        }

        clientApp.getGameClient().joinRoom(name, code);
    }

    @Override
    public void onShow() {
        nameField.clear();
        roomCodeField.clear();
    }
}
