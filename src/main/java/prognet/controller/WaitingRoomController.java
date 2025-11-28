package prognet.controller;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import prognet.App;

public class WaitingRoomController implements App.DataReceiver {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

    @FXML
    private Label roomCode1;

    @FXML
    private Label roomCode2;

    @FXML
    private Label roomCode3;

    @FXML
    private Label roomCode4;

    @FXML
    private Label roomCode5;

    @FXML
    private Label roomCode6;

    @FXML
    private Label animatedDots;

    private String roomCode;
    private Timeline dotsAnimation;

    @FXML
    public void initialize() {
        // Setup button hover effects
        setupButtonHover(backBtn);

        // Start animated dots
        startDotsAnimation();
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof String) {
            setRoomCode((String) data);
        }
    }

    public void setRoomCode(String code) {
        this.roomCode = code;

        // Set each digit in the corresponding label
        if (code != null && code.length() == 6) {
            roomCode1.setText(String.valueOf(code.charAt(0)));
            roomCode2.setText(String.valueOf(code.charAt(1)));
            roomCode3.setText(String.valueOf(code.charAt(2)));
            roomCode4.setText(String.valueOf(code.charAt(3)));
            roomCode5.setText(String.valueOf(code.charAt(4)));
            roomCode6.setText(String.valueOf(code.charAt(5)));
        }
    }

    private void startDotsAnimation() {
        final String[] dotStates = {".", "..", "..."};
        final int[] currentState = {0};

        dotsAnimation = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            animatedDots.setText(dotStates[currentState[0]]);
            currentState[0] = (currentState[0] + 1) % dotStates.length;
        }));

        dotsAnimation.setCycleCount(Timeline.INDEFINITE);
        dotsAnimation.play();
    }

    private void setupButtonHover(Button button) {
        if (button == null) {
            return;
        }

        button.setOnMouseEntered(e -> {
            button.setOpacity(0.7);
        });

        button.setOnMouseExited(e -> {
            button.setOpacity(1.0);
        });
    }

    @FXML
    private void onBack() {
        // Stop animations
        if (dotsAnimation != null) {
            dotsAnimation.stop();
        }

        // TODO: Disconnect from server/room
        System.out.println("Leaving room: " + roomCode);

        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (dotsAnimation != null) {
            dotsAnimation.stop();
        }
    }
}
