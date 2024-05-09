import java.util.List;
import java.util.Set;

public class NumberleController {

    private INumberleModel model;

    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    public void setView(NumberleView view) {
        this.view = view;
    }

    public boolean processInput(String input) {
        return model.processInput(input);
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isGameWon() {
        return model.isGameWon();
    }

    public String getTargetWord() {
        return model.getTargetEquation();
    }

    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void startNewGame(boolean showEquation, boolean validateInput, boolean randomSelection) {
        model.initialize(model, showEquation ? 1 : 0, validateInput ? 1 : 0, randomSelection ? 1 : 0);

    }

    public boolean getDisplayTargetEquation() {
        return model.getDisplayTargetEquation();
    }

    public boolean getDisplayErrorIfInvalid() {
        return model.getDisplayErrorIfInvalid();
    }

    public boolean getUseRandomSelection() {
        return model.getUseRandomSelection();
    }

    public Set<Character> getCorrectPositions() {
        return model.getCorrectPositions();
    }

    public Set<Character> getWrongPositions() {
        return model.getWrongPositions();
    }

    public Set<Character> getNotInEquation() {
        return model.getNotInEquation();
    }

    public Set<Character> getUnused() {
        return model.getUnused();
    }

    public String getFeedback() {
        return model.getFeedback();
    }

}