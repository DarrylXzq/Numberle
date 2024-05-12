import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        System.out.println("  _   _                       _                     _             ____   _       ___ \n" +
                " | \\ | |  _   _   _ __ ___   | |__     ___   _ __  | |   ___     / ___| | |     |_ _|\n" +
                " |  \\| | | | | | | '_ ` _ \\  | '_ \\   / _ \\ | '__| | |  / _ \\   | |     | |      | | \n" +
                " | |\\  | | |_| | | | | | | | | |_) | |  __/ | |    | | |  __/   | |___  | |___   | | \n" +
                " |_| \\_|  \\__,_| |_| |_| |_| |_.__/   \\___| |_|    |_|  \\___|    \\____| |_____| |___|\n" +
                "                                                                                     ");
        Scanner scanner = new Scanner(System.in);
        // Create a new instance of the NumberleModel class
        INumberleModel model = new NumberleModel();

        // read user input to determine the game settings
        int showEquation = readBinaryInput(scanner, "🚩Whether to show the equation (0=No, 1=Yes): ");
        int validateInput = readBinaryInput(scanner, "🚩Whether to validate input (0=No, 1=Yes): ");
        int randomSelection = readBinaryInput(scanner, "🚩Whether to use random selection (0=No, 1=Yes): ");

        // initialize the model
        model.initialize(model, showEquation, validateInput, randomSelection);
        gameLogic(model, scanner);
        scanner.close();
    }

    private static void gameLogic(INumberleModel model, Scanner scanner) {
        System.out.println("\n✨✨Welcome to Numberle!✨✨" +
                "\n🔢You have " + model.MAX_ATTEMPTS + " attempts to guess the equation!🔢");

        //enter the game logic
        while (!model.isGameOver()) {
            displayTargetEquation(model);
            System.out.print("Please enter your guess: ");
            String input = scanner.nextLine();

            // process the input
            if (!model.processInput(input)) {
                for (int index : model.getErrorIndices()) {
                    System.out.println(model.getErrorMessages().get(index));
                }
                continue;
            }

            // check if the game is won
            if (model.isGameWon()) {
                System.out.println("CurrentGuess Result: " + model.getCurrentGuess());
                displaySets(model);
                System.out.println("\n🤗Congratulations! You have guessed the target equation correctly!");
                break;
            }

            // display the current guess and remaining attempts
            System.out.println("CurrentGuess Result: " + model.getCurrentGuess());
            displaySets(model);
            System.out.println("Remaining Attempts: " + model.getRemainingAttempts());
        }

        // display the result of the game
        if (!model.isGameWon()) {
            System.out.println("\n😭Unfortunately, you did not guess the target equation correctly. The target equation is:" + model.getTargetEquation());
        }

        System.out.println("\n✨✨Game Over, Thank you for playing Numberle!✨✨");
    }

    private static int readBinaryInput(Scanner scanner, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine(); // read the input
            if (input.equals("0") || input.equals("1")) {
                return Integer.parseInt(input); // convert the input to integer
            }
            System.out.println("⚠️invalid input, please enter 0 or 1!");
        }
    }

    private static void displayTargetEquation(INumberleModel model) {
        if (model.getDisplayTargetEquation()) {
            System.out.println("\n💡Target Equation is: " + model.getTargetEquation());
        }
    }

    private static void displaySets(INumberleModel model) {
        // define the colors for the sets
        String green = "\033[32m";  // Green for correct positions
        String orange = "\033[93m"; // Bright Yellow (orange-like) for wrong positions
        String gray = "\033[90m";   // Gray for not in equation
        String white = "\033[97m";  // White for unused
        String reset = "\033[0m";   // Reset to default color

        // display the sets
        System.out.println(green + "correctPositions: " + model.getCorrectPositions());
        System.out.println(orange + "wrongPositions: " + model.getWrongPositions());
        System.out.println(gray + "notInEquation: " + model.getNotInEquation());
        System.out.println(white + "unused: " + model.getUnused() + reset);
    }
}