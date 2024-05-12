import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class INumberleModelTest {

    /**The TargetEquation is fixed to 2+3*2=8，the first equation in the list，convenient for testing.
     *
     */

    private INumberleModel model;

    // Set up the model before each test
    @BeforeEach
    void setUp() {
        model = new NumberleModel();
    }

    // Clean up the model after each test
    @AfterEach
    void tearDown() {
        model = null;
    }

    /**
     * testGameRestart() Scenario:
     * After the game starts, check the game restart by inputting a correct equation but not the target equation, then restart the game.
     */

    @Test
    public void testGameRestart() {
        model.initialize(model, 1, 1, 0); // initialize the model,do not need to randomly select the equation
        // check the initial values
        checkInitialValues(model);

        model.processInput("1+5=2+4"); // process a correct processInput,but not the target equation
        assertEquals(5, model.getRemainingAttempts(), "Remaining attempts should decrease by 1.");
        assertFalse(model.isGameWon(), "Game should not be won.");

        // prepare the expected sets
        Set<Character> expectedCorrectPositions = new HashSet<>(Arrays.asList('+', '2'));
        Set<Character> expectedWrongPositions = new HashSet<>(List.of('='));
        Set<Character> expectedNotInEquation = new HashSet<>((Arrays.asList('1', '4', '5')));
        Set<Character> expectedUnused = new HashSet<>(Arrays.asList('0', '3', '6', '7', '8', '9', '-', '*', '/'));

        // check the sets if they match the expected values
        checkSetsIsMatch(model, expectedCorrectPositions, expectedWrongPositions, expectedNotInEquation, expectedUnused);

        model.restartGame(); // restart the game

        assertEquals(6, model.getRemainingAttempts(), "Remaining attempts should be reset to 6.");
        assertFalse(model.isGameWon(), "Game won should be reset to false.");

        // check the initial values
        checkInitialValues(model);

        // check the sets is empty
        checkSetsIsEmpty(model);
    }

    /**
     * testInvalidInput() Scenario:
     * After the game starts, check the invalid input by inputting
     * an invalid equation, too short equation, too long equation, missing equal sign, continuous operators, and result is not match.
     */
    @Test
    public void testInvalidInput(){
        model.initialize(model, 1, 1, 0); // initialize the model,do not need to randomly select the equation
        // check the initial values
        checkInitialValues(model);

        // prepare the expected equation
        List<String> ERROR_EQUATIONS = List.of(
                "1+2=3%4",    // invalid character
                "1+2=3",      // too short
                "1+2=3+4+5",  // too long
                "1+2+3-1",    // missing equal sign
                "1+-3=+4",    // operators is continuous
                "1+2=3+4"     // result is not match
        );

        // prepare the expected errors index
        List<Integer> expectedErrors = List.of(
                0, // invalid character
                1, // too short
                2, // too long
                3, // missing equal sign
                5, // operators is continuous
                6  // result is not match
        );

        // process the invalid input
        for (int i = 0; i < ERROR_EQUATIONS.size(); i++) {
            String equation = ERROR_EQUATIONS.get(i);
            model.processInput(equation);
            // check the remaining attempts, game status
            assertEquals(6, model.getRemainingAttempts(), "The attempts should not decrease.");
            assertFalse(model.isGameWon(), "Game should not be won.");
            assertFalse(model.isGameOver(), "Game should not be over.");

            // check the error indices
            List<Integer> errorIndices = model.getErrorIndices();
            assertEquals(1, errorIndices.size(), "Should only have one error per input.");
            assertEquals(expectedErrors.get(i), errorIndices.get(0), "Error index should match expected for equation: " + equation);

            // check sets is empty
            checkSetsIsEmpty(model);
        }
    }

    /**
     * testGameOverConditions() Scenario:
     * After the game starts, check the game over conditions by inputting an incorrect equation until the maximum number of attempts is reached.
     */
    @Test
    public void testGameOverConditions() {
        model.initialize(model, 1, 1, 0); // initialize the model,do not need to randomly select the equation
        // check the initial values
        checkInitialValues(model);

        // prepare the expected sets
        Set<Character> expectedCorrectPositions = new HashSet<>(Arrays.asList('+', '3'));
        Set<Character> expectedWrongPositions = new HashSet<>(Arrays.asList('=', '2'));
        Set<Character> expectedNotInEquation = new HashSet<>((Arrays.asList('4', '5')));
        Set<Character> expectedUnused = new HashSet<>(Arrays.asList('0', '1', '6', '7', '8', '9', '-', '*', '/'));

        for (int i = 1; i <= 6; i++) {
            model.processInput("4+3=5+2"); // process an incorrect equation
            assertEquals(6 - i, model.getRemainingAttempts(), "Remaining attempts should decrease by 1.");
            assertFalse(model.isGameWon(), "Game should not be won.");
            checkSetsIsMatch(model, expectedCorrectPositions, expectedWrongPositions, expectedNotInEquation, expectedUnused);
        }
        assertTrue(model.isGameOver(), "Game should be over after max attempts.");
    }

    // Helper method to check if the sets match the expected values
    private void checkSetsIsMatch(INumberleModel model, Set<Character> expectedCorrectPositions, Set<Character> expectedWrongPositions, Set<Character> expectedNotInEquation, Set<Character> expectedUnused) {
        assertEquals(expectedCorrectPositions, model.getCorrectPositions(), "Correct positions should match expected.");
        assertEquals(expectedWrongPositions, model.getWrongPositions(), "Wrong positions should match expected.");
        assertEquals(expectedNotInEquation, model.getNotInEquation(), "Not in equation should match expected.");
        assertEquals(expectedUnused, model.getUnused(), "Unused characters should match expected.");
    }

    // Helper method to check if the sets are empty
    private void checkSetsIsEmpty(INumberleModel model) {
        assertTrue(model.getCorrectPositions().isEmpty(), "Correct positions should be reset.");
        assertTrue(model.getWrongPositions().isEmpty(), "Wrong positions should be reset.");
        assertTrue(model.getNotInEquation().isEmpty(), "Not in equation should be reset.");
        assertTrue(model.getUnused().containsAll(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '=')), "All characters should be marked as unused after game restart.");
    }

    // Helper method to check the initial values
    private void checkInitialValues(INumberleModel model) {
        assertTrue(model.getDisplayTargetEquation(), "Display target equation should be set to true.");
        assertTrue(model.getDisplayErrorIfInvalid(), "Display error if invalid should be set to true.");
        assertFalse(model.getUseRandomSelection(), "Use random selection should be set to false.");
    }


}