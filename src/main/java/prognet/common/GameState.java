package prognet.common;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomCode;
    private String player1Name;
    private String player2Name;
    private int player1Score;
    private int player2Score;
    private int currentTurn; // 1 or 2
    private String gridSize;
    private String theme;
    private List<Card> cards;
    private boolean gameStarted;
    private boolean gameOver;

    public GameState(String roomCode, String gridSize, String theme) {
        this.roomCode = roomCode;
        this.gridSize = gridSize;
        this.theme = theme;
        this.player1Score = 0;
        this.player2Score = 0;
        this.currentTurn = 1;
        this.gameStarted = false;
        this.gameOver = false;
    }

    // Getters
    public String getRoomCode() {
        return roomCode;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public String getGridSize() {
        return gridSize;
    }

    public String getTheme() {
        return theme;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Setters
    public void setPlayer1Name(String name) {
        this.player1Name = name;
    }

    public void setPlayer2Name(String name) {
        this.player2Name = name;
    }

    public void setPlayer1Score(int score) {
        this.player1Score = score;
    }

    public void setPlayer2Score(int score) {
        this.player2Score = score;
    }

    public void setCurrentTurn(int turn) {
        this.currentTurn = turn;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setGameStarted(boolean started) {
        this.gameStarted = started;
    }

    public void setGameOver(boolean over) {
        this.gameOver = over;
    }

    public void incrementScore(int player) {
        if (player == 1)
            player1Score++;
        else
            player2Score++;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }
}
