import java.util.List;
import java.util.Scanner;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;

    int readBinaryInput(Scanner scanner, String prompt);

    void initialize(INumberleModel model, int showEquation, int validateInput, int randomSelection);

    void gameLogic(INumberleModel model);

    void startNewGame();

    boolean processInput(String input);

    boolean isGameOver();

    boolean isGameWon();

    String getTargetEquation();

    StringBuilder getCurrentGuess();

    int getRemainingAttempts();

    // 新增方法
    void setDisplayTargetEquation(boolean displayTargetEquation);

    void setDisplayErrorIfInvalid(boolean displayErrorIfInvalid);

    void setUseRandomSelection(boolean useRandomSelection);
}