package prognet.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsController {

    // Grid Size Buttons
    @FXML
    private Button gridSize3x3Btn;
    @FXML
    private Button gridSize4x4Btn;
    @FXML
    private Button gridSize5x5Btn;

    // Theme Buttons
    @FXML
    private Button themeJungleBtn;
    @FXML
    private Button themeForestBtn;
    @FXML
    private Button themeSavannaBtn;
    @FXML
    private Button themeOceanBtn;

    // Card Pattern Buttons
    @FXML
    private Button patternPawsBtn;
    @FXML
    private Button patternLeavesBtn;
    @FXML
    private Button patternStripesBtn;
    @FXML
    private Button patternGeometricBtn;

    private Runnable onCloseHandler;

    // Current selections
    private String selectedGridSize = "4x4";
    private String selectedTheme = "Jungle";
    private String selectedPattern = "Paws";

    public void setOnClose(Runnable handler) {
        this.onCloseHandler = handler;
    }

    @FXML
    public void initialize() {
        // Initial selection state is already set in FXML
    }

    // Grid Size Selection
    @FXML
    private void onSelectGridSize3x3() {
        selectGridSize(gridSize3x3Btn, "3x3");
    }

    @FXML
    private void onSelectGridSize4x4() {
        selectGridSize(gridSize4x4Btn, "4x4");
    }

    @FXML
    private void onSelectGridSize5x5() {
        selectGridSize(gridSize5x5Btn, "5x5");
    }

    private void selectGridSize(Button selectedBtn, String size) {
        selectedGridSize = size;
        // Reset all grid size buttons
        gridSize3x3Btn.getStyleClass().remove("setting-option-btn-selected");
        gridSize3x3Btn.getStyleClass().add("setting-option-btn");
        gridSize4x4Btn.getStyleClass().remove("setting-option-btn-selected");
        gridSize4x4Btn.getStyleClass().add("setting-option-btn");
        gridSize5x5Btn.getStyleClass().remove("setting-option-btn-selected");
        gridSize5x5Btn.getStyleClass().add("setting-option-btn");

        // Set selected button
        selectedBtn.getStyleClass().remove("setting-option-btn");
        selectedBtn.getStyleClass().add("setting-option-btn-selected");

        System.out.println("Grid size selected: " + size);
    }

    // Theme Selection
    @FXML
    private void onSelectThemeJungle() {
        selectTheme(themeJungleBtn, "Jungle");
    }

    @FXML
    private void onSelectThemeForest() {
        selectTheme(themeForestBtn, "Forest");
    }

    @FXML
    private void onSelectThemeSavanna() {
        selectTheme(themeSavannaBtn, "Savanna");
    }

    @FXML
    private void onSelectThemeOcean() {
        selectTheme(themeOceanBtn, "Ocean");
    }

    private void selectTheme(Button selectedBtn, String theme) {
        selectedTheme = theme;
        // Reset all theme buttons
        themeJungleBtn.getStyleClass().remove("setting-option-btn-selected");
        themeJungleBtn.getStyleClass().add("setting-option-btn");
        themeForestBtn.getStyleClass().remove("setting-option-btn-selected");
        themeForestBtn.getStyleClass().add("setting-option-btn");
        themeSavannaBtn.getStyleClass().remove("setting-option-btn-selected");
        themeSavannaBtn.getStyleClass().add("setting-option-btn");
        themeOceanBtn.getStyleClass().remove("setting-option-btn-selected");
        themeOceanBtn.getStyleClass().add("setting-option-btn");

        // Set selected button
        selectedBtn.getStyleClass().remove("setting-option-btn");
        selectedBtn.getStyleClass().add("setting-option-btn-selected");

        System.out.println("Theme selected: " + theme);
    }

    // Pattern Selection
    @FXML
    private void onSelectPatternPaws() {
        selectPattern(patternPawsBtn, "Paws");
    }

    @FXML
    private void onSelectPatternLeaves() {
        selectPattern(patternLeavesBtn, "Leaves");
    }

    @FXML
    private void onSelectPatternStripes() {
        selectPattern(patternStripesBtn, "Stripes");
    }

    @FXML
    private void onSelectPatternGeometric() {
        selectPattern(patternGeometricBtn, "Geometric");
    }

    private void selectPattern(Button selectedBtn, String pattern) {
        selectedPattern = pattern;
        // Reset all pattern buttons
        patternPawsBtn.getStyleClass().remove("setting-option-btn-selected");
        patternPawsBtn.getStyleClass().add("setting-option-btn");
        patternLeavesBtn.getStyleClass().remove("setting-option-btn-selected");
        patternLeavesBtn.getStyleClass().add("setting-option-btn");
        patternStripesBtn.getStyleClass().remove("setting-option-btn-selected");
        patternStripesBtn.getStyleClass().add("setting-option-btn");
        patternGeometricBtn.getStyleClass().remove("setting-option-btn-selected");
        patternGeometricBtn.getStyleClass().add("setting-option-btn");

        // Set selected button
        selectedBtn.getStyleClass().remove("setting-option-btn");
        selectedBtn.getStyleClass().add("setting-option-btn-selected");

        System.out.println("Pattern selected: " + pattern);
    }

    public String getSelectedGridSize() {
        return selectedGridSize;
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public String getSelectedPattern() {
        return selectedPattern;
    }
}
