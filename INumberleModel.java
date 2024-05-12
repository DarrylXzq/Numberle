import java.util.List;
import java.util.Scanner;
import java.util.Set;

public interface INumberleModel {
    // Constants for the game attempts
    int MAX_ATTEMPTS = 6;

    void initialize(INumberleModel model, int showEquation, int validateInput, int randomSelection);

    void restartGame();

    boolean processInput(String input);

    boolean isGameOver();

    boolean isGameWon();

    String getTargetEquation();

    StringBuilder getCurrentGuess();

    int getRemainingAttempts();

    void setDisplayTargetEquation(boolean displayTargetEquation);

    void setDisplayErrorIfInvalid(boolean displayErrorIfInvalid);

    void setUseRandomSelection(boolean useRandomSelection);

    boolean getDisplayTargetEquation();

    Set<Character> getCorrectPositions();

    Set<Character> getWrongPositions();

    Set<Character> getNotInEquation();

    Set<Character> getUnused();

    String getFeedback();

    List<Integer> getErrorIndices();

    List<String> getErrorMessages();

}