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
    private int player1TotalWins; // Total match wins across multiple rounds
    private int player2TotalWins; // Total match wins across multiple rounds
    private boolean player1WantsRematch;
    private boolean player2WantsRematch;

    public GameState(String roomCode, String gridSize, String theme) {
        this.roomCode = roomCode;
        this.gridSize = gridSize;
        this.theme = theme;
        this.player1Score = 0;
        this.player2Score = 0;
        this.currentTurn = 1;
        this.gameStarted = false;
        this.gameOver = false;
        this.player1TotalWins = 0;
        this.player2TotalWins = 0;
        this.player1WantsRematch = false;
        this.player2WantsRematch = false;
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

    // Total wins getters and setters
    public int getPlayer1TotalWins() {
        return player1TotalWins;
    }

    public void setPlayer1TotalWins(int wins) {
        this.player1TotalWins = wins;
    }

    public int getPlayer2TotalWins() {
        return player2TotalWins;
    }

    public void setPlayer2TotalWins(int wins) {
        this.player2TotalWins = wins;
    }

    public void incrementPlayer1TotalWins() {
        this.player1TotalWins++;
    }

    public void incrementPlayer2TotalWins() {
        this.player2TotalWins++;
    }

    // Rematch vote getters and setters
    public boolean isPlayer1WantsRematch() {
        return player1WantsRematch;
    }

    public void setPlayer1WantsRematch(boolean wants) {
        this.player1WantsRematch = wants;
    }

    public boolean isPlayer2WantsRematch() {
        return player2WantsRematch;
    }

    public void setPlayer2WantsRematch(boolean wants) {
        this.player2WantsRematch = wants;
    }

    public void resetRematchVotes() {
        this.player1WantsRematch = false;
        this.player2WantsRematch = false;
    }

    public void resetRoundScores() {
        this.player1Score = 0;
        this.player2Score = 0;
    }

    public void resetTotalWins() {
        this.player1TotalWins = 0;
        this.player2TotalWins = 0;
    }
}
