package prognet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SettingsController {

    @FXML
    private Slider soundVolumeSlider;

    @FXML
    private Label soundVolumeLabel;

    @FXML
    private CheckBox soundEffectsCheckBox;

    @FXML
    private Slider musicVolumeSlider;

    @FXML
    private Label musicVolumeLabel;

    @FXML
    private CheckBox backgroundMusicCheckBox;

    @FXML
    private CheckBox animationsCheckBox;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Runnable onCloseHandler;

    public void setOnClose(Runnable handler) {
        this.onCloseHandler = handler;
    }

    @FXML
    public void initialize() {
        // Setup slider listeners to update labels
        soundVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            soundVolumeLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicVolumeLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        // Disable sliders when checkboxes are unchecked
        soundVolumeSlider.disableProperty().bind(soundEffectsCheckBox.selectedProperty().not());
        musicVolumeSlider.disableProperty().bind(backgroundMusicCheckBox.selectedProperty().not());
    }

    @FXML
    private void onSave() {
        // TODO: Save settings to preferences/config file
        System.out.println("Settings saved:");
        System.out.println("  Sound Effects: " + soundEffectsCheckBox.isSelected()
                + " (Volume: " + (int) soundVolumeSlider.getValue() + "%)");
        System.out.println("  Background Music: " + backgroundMusicCheckBox.isSelected()
                + " (Volume: " + (int) musicVolumeSlider.getValue() + "%)");
        System.out.println("  Animations: " + animationsCheckBox.isSelected());

        closeDialog();
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        if (onCloseHandler != null) {
            onCloseHandler.run();
        }
    }
}
