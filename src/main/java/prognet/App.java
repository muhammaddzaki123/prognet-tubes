package prognet;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Object controllerData;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("home"), 650, 700);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Memory Game - Multiplayer");
        stage.centerOnScreen();
        stage.show();

    }

    static public void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static public void setRoot(String fxml, Object data) throws IOException {
        controllerData = data;
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent parent = fxmlLoader.load();

        // Pass data to controller if available
        if (controllerData != null) {
            Object controller = fxmlLoader.getController();
            if (controller instanceof DataReceiver) {
                ((DataReceiver) controller).receiveData(controllerData);
            }
            controllerData = null; // Clear after use
        }

        return parent;
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Interface for controllers that can receive data
     */
    public interface DataReceiver {

        void receiveData(Object data);
    }
}
