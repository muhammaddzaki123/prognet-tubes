package prognet.controller;

import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class BaseController {

    @FXML
    private Label topLeftAnimal;

    @FXML
    private Label topRightAnimal;

    @FXML
    private Label bottomLeftAnimal;

    @FXML
    private Label bottomRightAnimal;

    public void initialize() {

        // Animate top left animal (Tiger)
        animateFloat(topLeftAnimal, -30, 1000, 0);

        // Animate top right animal (Bird)
        animateFloat(topRightAnimal, -35, 1500, 500);

        // Animate bottom left animal (Sloth)
        animateFloat(bottomLeftAnimal, -32, 1200, 300);

        // Animate bottom right animal (Monkey)
        animateFloat(bottomRightAnimal, -28, 1800, 700);
    }

    private void animateFloat(Label label, double distance, int duration, int delay) {
        if (label == null) {
            return;
        }

        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), label);
        transition.setFromY(0);
        transition.setToY(distance);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setDelay(Duration.millis(delay));
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        // Enable cache for better performance
        label.setCache(true);
        label.setCacheHint(javafx.scene.CacheHint.SPEED);

        transition.play();
    }

}
