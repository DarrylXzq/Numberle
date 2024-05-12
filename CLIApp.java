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

        System.out.println("\n✨✨Welcome to Numberle!✨✨" +
                "\n🔢You have " + model.MAX_ATTEMPTS + " attempts to guess the equation!🔢");

        //enter the game logic
        while (!model.isGameOver()) {
            model.displayTargetEquation();
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
                model.displaySets();
                System.out.println("\n🤗Congratulations! You have guessed the target equation correctly!");
                break;
            }

            // display the current guess and remaining attempts
            System.out.println("CurrentGuess Result: " + model.getCurrentGuess());
            model.displaySets();
            System.out.println("Remaining Attempts: " + model.getRemainingAttempts());
        }

        // display the result of the game
        if (!model.isGameWon()) {
            System.out.println("\n😭Unfortunately, you did not guess the target equation correctly. The target equation is:" + model.getTargetEquation());
        }

        System.out.println("\n✨✨Game Over, Thank you for playing Numberle!✨✨");
        scanner.close();
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
}