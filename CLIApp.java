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
        int showEquation = model.readBinaryInput(scanner, "🚩Whether to show the equation (0=No, 1=Yes): ");
        int validateInput = model.readBinaryInput(scanner, "🚩Whether to validate input (0=No, 1=Yes): ");
        int randomSelection = model.readBinaryInput(scanner, "🚩Whether to use random selection (0=No, 1=Yes): ");

        // initialize the model
        model.initialize(model, showEquation, validateInput, randomSelection);

        System.out.println("\n✨✨Welcome to Numberle!✨✨" +
                "\n🔢You have " + model.MAX_ATTEMPTS + " attempts to guess the equation!🔢");

        //enter the game logic
        model.gameLogic(model);
        scanner.close();
    }
}