import java.util.*;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class NumberleModel extends Observable implements INumberleModel {
    //@ invariant MAX_ATTEMPTS == 6;
    //@ invariant validEquations != null && \forall String eq; validEquations.contains(eq); eq != null && eq.matches("[0-9\\+\\-\\*/=]*");
    //@ invariant 0 <= remainingAttempts && remainingAttempts <= MAX_ATTEMPTS;
    //@ invariant currentGuess != null && currentGuess.length() == 7;
    //@ invariant gameWon == true || gameWon == false;

    //define four sets to store the characters
    private final Set<Character> correctPositions = new LinkedHashSet<>();
    private final Set<Character> wrongPositions = new LinkedHashSet<>();
    private final Set<Character> notInEquation = new LinkedHashSet<>();
    private final Set<Character> unused = new LinkedHashSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '='));

    // define target equation and current guess and remaining attempts and game status
    private String targetEquation;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;

    // define the three boolean variables to initialize the model
    private boolean displayErrorIfInvalid;
    private boolean displayTargetEquation;
    private boolean useRandomSelection;

    // define the list to store the equation list
    private List<String> validEquations;

    // define the patterns to check the input equation
    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[0-9\\+\\-\\*/=]*$");
    private static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("=");
    private static final Pattern ARITHMETIC_OPERATORS_PATTERN = Pattern.compile(".*[\\+\\-\\*/].");
    private static final Pattern OPERATOR_SEQUENCE_PATTERN = Pattern.compile("[\\+\\-\\*/]{2,}");

    //store the equation color feedback
    private String Feedback;

    //store the error message index
    private final List<Integer> errorIndices = new ArrayList<>();

    //store the error message list
    private final List<String> ERROR_MESSAGES = List.of(
            "⚠️ The equation contains illegal characters.",
            "⚠️ The equation is too short; it must be at least 7 characters long.",
            "⚠️ The equation is too long; it must not exceed 7 characters.",
            "⚠️ The equation must contain an equal sign.",
            "⚠️ The equation lacks an operator.",
            "⚠️ Operators in the equation appear consecutively.",
            "⚠️ The calculated results on both sides of the equality do not match."
    );

    /**
     * Resets the game settings and starts a new game.
     *
     * @requires validEquations != null && !validEquations.isEmpty()
     * @ensures remainingAttempts == MAX_ATTEMPTS && gameWon == false && currentGuess.length() == 7
     * @assignable remainingAttempts, gameWon, currentGuess;
     */
    @Override
    public void restartGame() {
        assert validEquations != null && !validEquations.isEmpty() : "Precondition failed: Valid equations must not be null or empty";

        initializeSets();
        startNewGame();

        assert remainingAttempts == MAX_ATTEMPTS && !gameWon && currentGuess.length() == 7 : "Postcondition failed: Game state not reset properly";
        assert validEquations != null : "Invariant violation: validEquations is null";
    }


    /**
     * Configures and initializes the game model with the provided settings and starts a new game.
     *
     * @requires model != null && (showEquation == 0 || showEquation == 1) &&
     * (validateInput == 0 || validateInput == 1) && (randomSelection == 0 || randomSelection == 1)
     * @ensures displayTargetEquation == (showEquation == 1) &&
     * displayErrorIfInvalid == (validateInput == 1) &&
     * useRandomSelection == (randomSelection == 1)
     * @assignable displayTargetEquation, displayErrorIfInvalid, useRandomSelection;
     */
    @Override
    public void initialize(INumberleModel model, int showEquation, int validateInput, int randomSelection) {
        assert model != null && (showEquation == 0 || showEquation == 1) &&
                (validateInput == 0 || validateInput == 1) && (randomSelection == 0 || randomSelection == 1) : "Precondition failed: Invalid initialization parameters";

        loadValidEquations();
        configureModel(model, showEquation, validateInput, randomSelection);
        restartGame();

        assert displayTargetEquation == (showEquation == 1) && displayErrorIfInvalid == (validateInput == 1) &&
                useRandomSelection == (randomSelection == 1) : "Postcondition failed: Model settings not applied correctly";
    }


    /**
     * Processes the input from the user and updates the game state based on the validity of the input.
     *
     * @requires input != null
     * @ensures (!\old(isValidEquation(input)) ==> \result == false) &&
     *          (\old(isValidEquation(input)) ==> \result == true) &&
     *          (\old(isValidEquation(input)) ==> remainingAttempts == \old(remainingAttempts) - 1) &&
     *          (\old(input.equals(targetEquation)) ==> gameWon == true)
     * @assignable remainingAttempts, gameWon;
     */
    @Override
    public boolean processInput(String input) {
        assert input != null : "Precondition failed: Input cannot be null";

        if (!isValidEquation(input)) {
            for (int index : errorIndices) {
                setChanged();
                notifyObservers(ERROR_MESSAGES.get(index));
            }
            assert !gameWon : "Postcondition failed: Game should not be won on invalid input";
            return false;  // directly return false if the input is invalid
        }

        remainingAttempts--;  // reduce the remaining attempts
        // notify the observer to check restart button
        setChanged();
        notifyObservers("Restart");
        updateCurrentGuess(input);

        // check if the input is the target equation
        if (input.equals(targetEquation)) {
            gameWon = true;
            //notify the observer game won
            setChanged();
            notifyObservers("Congratulations on guessing the equation correctly!");
        }
        //notify the observer to check game failed
        setChanged();
        notifyObservers("CheckGameFailed");
        assert (input.equals(targetEquation) == gameWon) : "Postcondition failed: Game won state mismatch";
        return true;
    }


    /**
     * Checks if the game is over based on remaining attempts or if the game has been won.
     *
     * @ensures \result == (remainingAttempts <= 0 || gameWon)
     */
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    /**
     * Checks if the game has been won.
     *
     * @ensures \result == gameWon
     */
    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    // Initializes a new game with a target equation either randomly selected or from the start of the list.
    private void startNewGame() {
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        currentGuess = new StringBuilder("       ");
        //check if the target equation is randomly selected
        if (useRandomSelection) {
            Random rand = new Random();
            targetEquation = validEquations.get(rand.nextInt(validEquations.size()));
        } else {
            targetEquation = validEquations.get(0); // always use the first equation in the list
        }
    }

    // Configures the model with the provided settings
    private void configureModel(INumberleModel model, int showEquation, int validateInput, int randomSelection) {
        model.setDisplayTargetEquation(showEquation == 1);
        model.setDisplayErrorIfInvalid(validateInput == 1);
        model.setUseRandomSelection(randomSelection == 1);
        setChanged();
        notifyObservers();
    }

    // initialize the four sets
    private void initializeSets() {
        correctPositions.clear();
        wrongPositions.clear();
        notInEquation.clear();
        unused.clear();
        for (char c : "0123456789+-*/=".toCharArray()) {
            unused.add(c);
        }
    }

    private void loadValidEquations() {
        // read the equations from the file
        try {
            validEquations = Files.readAllLines(Paths.get("equations.txt"));
        } catch (IOException e) {
            System.err.println("Error reading equations from file: " + e.getMessage());
            validEquations = new ArrayList<>();
        }
    }

    private String evaluateFeedback(String input) {
        // initialize the feedback string
        StringBuilder feedback = new StringBuilder("       ");
        // convert the input and target equation to char arrays
        char[] inputChars = input.toCharArray();
        char[] targetChars = targetEquation.toCharArray();

        // first check for correct positions
        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == targetChars[i]) {
                feedback.setCharAt(i, 'G'); // Green indicates correct position
                targetChars[i] = ' '; // mark as used
            }
        }

        // then check for wrong positions
        for (int i = 0; i < inputChars.length; i++) {
            if (feedback.charAt(i) == 'G') continue; // skip correct positions
            for (int j = 0; j < targetChars.length; j++) {
                if (inputChars[i] == targetChars[j] && targetChars[j] != ' ') {
                    feedback.setCharAt(i, 'O'); // Orange indicates wrong position
                    targetChars[j] = ' '; // mark as used
                    break;
                }
            }
        }

        // finally, mark the remaining characters as not in the equation
        for (int i = 0; i < feedback.length(); i++) {
            if (feedback.charAt(i) != 'G' && feedback.charAt(i) != 'O') {
                feedback.setCharAt(i, 'X'); // Grey indicates not in equation
            }
        }
        Feedback = feedback.toString();
        setChanged();
        notifyObservers("Feedback");
        return feedback.toString();
    }

    private boolean isValidEquation(String equation) {
        errorIndices.clear(); // clear the error indices

        // First step: check if the input contains valid characters
        if (!VALID_CHARS_PATTERN.matcher(equation).matches()) {
            if (displayErrorIfInvalid) {
                errorIndices.add(0);
            }
            return false;
        }

        // Second step: check if the input is too short or too long
        if (equation.length() < 7) {
            if (displayErrorIfInvalid) {
                errorIndices.add(1);
            }
            return false;
        } else if (equation.length() > 7) {
            if (displayErrorIfInvalid) {
                errorIndices.add(2);
            }
            return false;
        }

        // Third step: check if the input contains an equal sign
        if (!EQUAL_SIGN_PATTERN.matcher(equation).find()) {
            if (displayErrorIfInvalid) {
                errorIndices.add(3);
            }
            return false;
        }

        // Fourth step: check if the input contains an operator
        if (!ARITHMETIC_OPERATORS_PATTERN.matcher(equation).find()) {
            if (displayErrorIfInvalid) {
                errorIndices.add(4);
            }
            return false;
        }

        // Fifth step: check if the input contains consecutive operators
        if (OPERATOR_SEQUENCE_PATTERN.matcher(equation).find()) {
            if (displayErrorIfInvalid) {
                errorIndices.add(5);
            }
            return false;
        }

        if (displayErrorIfInvalid) {
            // Sixth step: check if the calculated results on both sides of the equality match
            try {
                String[] parts = equation.split("=");
                if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                    errorIndices.add(6);
                    return false;
                }
                double leftResult = evaluateExpression(parts[0]);
                double rightResult = evaluateExpression(parts[1]);
                if (Math.abs(leftResult - rightResult) > 0.0001) { // allow for a small margin of error
                    errorIndices.add(6);
                    return false;
                }
            } catch (Exception e) {
                errorIndices.add(6);
                return false;
            }
        }
        return true;
    }

    private void updateCurrentGuess(String input) {
        String feedback = evaluateFeedback(input);
        String[] colors = {"\033[32m", // Green
                "\033[93m", // Bright Yellow (for a vivid orange-like color)
                "\033[90m", // Bright Black (for gray)
                "\033[0m"}; // Reset

        // clear the current guess
        currentGuess.setLength(0);

        // iterate over the feedback characters
        for (int i = 0; i < feedback.length(); i++) {
            char feedbackChar = feedback.charAt(i);
            String color = switch (feedbackChar) {
                case 'G' -> colors[0];  // Green
                case 'O' -> colors[1];  // Orange
                default -> colors[2];   // grey for 'X' or any other
            };

            // append the color code, the input character, and the reset code
            currentGuess.append(color).append(input.charAt(i)).append(colors[3]);
            updateSets(input, feedback);
        }
    }

    private void updateSets(String input, String feedback) {
        correctPositions.clear();
        wrongPositions.clear();
        notInEquation.clear();

        for (int i = 0; i < feedback.length(); i++) {
            char ch = input.charAt(i);
            switch (feedback.charAt(i)) {
                case 'G' -> {  // correct position
                    correctPositions.add(ch);
                    unused.remove(ch);
                }
                case 'O' -> {  // wrong position
                    if (!correctPositions.contains(ch)) {
                        wrongPositions.add(ch);
                    }
                    unused.remove(ch);
                }
                case 'X' -> {  // not in equation
                    if (!correctPositions.contains(ch) && !wrongPositions.contains(ch)) {
                        notInEquation.add(ch);
                    }
                    unused.remove(ch);
                }
            }
        }
        setChanged();
        notifyObservers("UpdateKeyboard");
    }

    // Following the BODMAS rule, evaluate the expression
    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int index = 0;
        while (index < expression.length()) {
            char c = expression.charAt(index);

            // process numbers
            if (Character.isDigit(c)) {
                StringBuilder numberBuilder = new StringBuilder();
                while (index < expression.length() && Character.isDigit(expression.charAt(index))) {
                    numberBuilder.append(expression.charAt(index++));
                }
                numbers.push(Double.parseDouble(numberBuilder.toString()));
                continue; // skip the index increment
            }

            // process operators
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }

            index++;
        }

        // process the remaining operators
        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char currentOp, char stackOp) {
        // check if the current operator has higher precedence than the operator on the stack
        return (currentOp != '*' && currentOp != '/') || (stackOp != '+' && stackOp != '-');
    }

    private double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            default -> throw new UnsupportedOperationException("Invalid operator: " + operator);
        };
    }


    @Override
    public String getTargetEquation() {
        return targetEquation;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }


    @Override
    public void setDisplayErrorIfInvalid(boolean displayErrorIfInvalid) {
        this.displayErrorIfInvalid = displayErrorIfInvalid;
    }

    @Override
    public void setDisplayTargetEquation(boolean displayTargetEquation) {
        this.displayTargetEquation = displayTargetEquation;
    }

    @Override
    public void setUseRandomSelection(boolean useRandomSelection) {
        this.useRandomSelection = useRandomSelection;
    }

    @Override
    public boolean getDisplayTargetEquation() {
        return displayTargetEquation;
    }

    @Override
    public Set<Character> getCorrectPositions() {
        return correctPositions;
    }

    @Override
    public Set<Character> getWrongPositions() {
        return wrongPositions;
    }

    @Override
    public Set<Character> getNotInEquation() {
        return notInEquation;
    }

    @Override
    public Set<Character> getUnused() {
        return unused;
    }

    @Override
    public String getFeedback() {
        return Feedback;
    }

    @Override
    public List<Integer> getErrorIndices() {
        return errorIndices;
    }

    @Override
    public List<String> getErrorMessages() {
        return ERROR_MESSAGES;
    }

}
