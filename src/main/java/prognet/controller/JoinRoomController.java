package prognet.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import prognet.App;

public class JoinRoomController {

    @FXML
    private StackPane rootContainer;

    @FXML
    private Button backBtn;

    @FXML
    private TextField digit1;

    @FXML
    private TextField digit2;

    @FXML
    private TextField digit3;

    @FXML
    private TextField digit4;

    @FXML
    private TextField digit5;

    @FXML
    private TextField digit6;

    @FXML
    private TextField fullCodeInput;

    @FXML
    private Button joinBtn;

    private TextField[] digitFields;

    @FXML
    public void initialize() {
        // Initialize digit fields array
        digitFields = new TextField[]{digit1, digit2, digit3, digit4, digit5, digit6};

        // Setup digit input behavior
        setupDigitInputs();

        // Setup full code input behavior
        setupFullCodeInput();

        // Setup button hover effects
        setupButtonHover(backBtn);
        setupJoinButtonHover();

        // Focus first digit on load
        Platform.runLater(() -> digit1.requestFocus());
    }

    private void setupDigitInputs() {
        for (int i = 0; i < digitFields.length; i++) {
            TextField field = digitFields[i];
            final int index = i;

            // Limit to single digit
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > 1) {
                    field.setText(newValue.substring(0, 1));
                }
                // Only allow digits
                if (!newValue.matches("\\d*")) {
                    field.setText(oldValue);
                }
                // Auto-focus next field
                if (newValue.length() == 1 && index < digitFields.length - 1) {
                    digitFields[index + 1].requestFocus();
                }
                // Update full code input and button state
                updateFullCodeFromDigits();
                updateJoinButtonState();
            });

            // Handle backspace to move to previous field
            field.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("BACK_SPACE")) {
                    if (field.getText().isEmpty() && index > 0) {
                        digitFields[index - 1].requestFocus();
                        digitFields[index - 1].selectAll();
                    }
                }
            });
        }
    }

    private void setupFullCodeInput() {
        // Limit to 6 digits
        fullCodeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 6) {
                fullCodeInput.setText(newValue.substring(0, 6));
            }
            // Only allow digits
            if (!newValue.matches("\\d*")) {
                fullCodeInput.setText(oldValue);
            }
            // Update individual digits
            updateDigitsFromFullCode();
            updateJoinButtonState();
        });
    }

    private void updateFullCodeFromDigits() {
        StringBuilder code = new StringBuilder();
        for (TextField field : digitFields) {
            code.append(field.getText());
        }
        fullCodeInput.setText(code.toString());
    }

    private void updateDigitsFromFullCode() {
        String code = fullCodeInput.getText();
        for (int i = 0; i < digitFields.length; i++) {
            if (i < code.length()) {
                digitFields[i].setText(String.valueOf(code.charAt(i)));
            } else {
                digitFields[i].setText("");
            }
        }
    }

    private void updateJoinButtonState() {
        String code = fullCodeInput.getText();
        boolean isValid = code.length() == 6;
        joinBtn.setDisable(!isValid);
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

    private void setupJoinButtonHover() {
        if (joinBtn == null) {
            return;
        }

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40 15 40;";

        joinBtn.setOnMouseEntered(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle("-fx-background-color: #2563EB; " + baseStyle
                        + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
            }
        });

        joinBtn.setOnMouseExited(e -> {
            if (!joinBtn.isDisabled()) {
                joinBtn.setStyle("-fx-background-color: #3B82F6; " + baseStyle);
            }
        });
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onJoinRoom() {
        String roomCode = fullCodeInput.getText();

        if (roomCode.length() != 6) {
            showAlert("Invalid Code", "Please enter a valid 6-digit room code.", AlertType.WARNING);
            return;
        }

        // TODO: Implement actual room joining logic with network client
        // For now, navigate to waiting room with room code
        try {
            App.setRoot("waitingroom", roomCode);
        } catch (IOException e) {
            showAlert("Error", "Failed to join room: " + e.getMessage(), AlertType.ERROR);
        }

        // Later: Add GameClient.connect(roomCode) logic here
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
