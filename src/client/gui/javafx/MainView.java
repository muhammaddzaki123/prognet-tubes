package client.gui.javafx;

import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import java.util.HashMap;
import java.util.Map;

public class MainView extends StackPane {
    private ClientApp clientApp;
    private Map<String, Node> views = new HashMap<>();

    // References to specific views
    private HomeView homeView;
    private CreateRoomView createRoomView;
    private JoinRoomView joinRoomView;
    private WaitingRoomView waitingRoomView;
    private GameBoardView gameBoardView;
    private GameResultView gameResultView;
    private HowToPlayView howToPlayView;

    public MainView(ClientApp clientApp) {
        this.clientApp = clientApp;
        initializeViews();
        showView("HOME");
    }

    private void initializeViews() {
        homeView = new HomeView(clientApp);
        createRoomView = new CreateRoomView(clientApp);
        joinRoomView = new JoinRoomView(clientApp);
        waitingRoomView = new WaitingRoomView(clientApp);
        gameBoardView = new GameBoardView(clientApp);
        gameResultView = new GameResultView(clientApp);
        howToPlayView = new HowToPlayView(clientApp);

        views.put("HOME", homeView);
        views.put("CREATE_ROOM", createRoomView);
        views.put("JOIN_ROOM", joinRoomView);
        views.put("WAITING_ROOM", waitingRoomView);
        views.put("GAME_BOARD", gameBoardView);
        views.put("GAME_RESULT", gameResultView);
        views.put("HOW_TO_PLAY", howToPlayView);
    }

    public void showView(String viewName) {
        getChildren().clear();
        if (views.containsKey(viewName)) {
            Node view = views.get(viewName);
            // Ideally call a "onShow" method if the view needs to refresh
            if (view instanceof Refreshable) {
                ((Refreshable) view).onShow();
            }
            getChildren().add(view);
        }
    }

    public CreateRoomView getCreateRoomView() { return createRoomView; }
    public JoinRoomView getJoinRoomView() { return joinRoomView; }
    public WaitingRoomView getWaitingRoomView() { return waitingRoomView; }
    public GameBoardView getGameBoardView() { return gameBoardView; }
    public GameResultView getGameResultView() { return gameResultView; }

    public interface Refreshable {
        void onShow();
    }
}
