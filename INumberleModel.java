import java.util.List;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    boolean processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
}