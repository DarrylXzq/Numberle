import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.stream.Stream;

public class NumberleView implements Observer {

    private final INumberleModel model;
    private final NumberleController controller;

    //The main frame
    private final JFrame frame = new JFrame("Numberle");
    //The Game Board
    private final JTextField[][] inputFields = new JTextField[6][7];

    // Colors for different feedback
    private final Color green = new Color(61, 191, 165);   // Green color, represents correct position
    private final Color orange = new Color(241, 155, 113); // Orange color, represents correct symbol but in the wrong position
    private final Color grey = new Color(165, 172, 195);   // Grey color, represents symbol not in the equation at all
    private final Color white = new Color(219, 223, 236);  // White color, represents unused buttons

    private final JButton hintButton; // Declare hintButton at class level
    private final JButton restartButton; // Declare restartButton at class level

    // Panels for the number and operation buttons of keyboard
    private JPanel numberPanel;
    private JPanel operationPanel;


    private boolean isFirstRun = true;  // Flag to check if the game is running for the first time

    boolean stop = false;//flag to check if the input is full

    private int currentPanelIndex = 0;//initialize the currentPanelIndex to 0
    private int currentInputIndex = 0;//initialize the currentInputIndex to 0

    // Constructor
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;

        // Create the hintButton and add an ActionListener
        hintButton = createButtonWithIcon("Hint", "./figure/hint.png", 30, 30);
        hintButton.addActionListener(e -> {
            String targetEquation = controller.getTargetWord();
            JOptionPane.showMessageDialog(frame, "Target Equation: " + targetEquation, "Hint", JOptionPane.INFORMATION_MESSAGE);
        });

        // Create the restartButton and add an ActionListener
        restartButton = createButtonWithIcon("Restart", "./figure/restart.png", 30, 30);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            restartButton.setEnabled(false);
            resetGameInterface();
            controller.restartGame();

        });

        // Add this view as an observer of the model
        ((NumberleModel) this.model).addObserver(this);
        this.controller.setView(this);
        update((NumberleModel) this.model, null);
        // Show the settings dialog
        showPreGameSettings();
        // Initialize the frame
        initializeFrame();
    }

    // Method to show the settings dialog before the game starts
    private void showPreGameSettings() {
        JDialog settingsDialog = new JDialog(frame, "Settings", true);
        // Set the size and location of the dialog
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setLayout(new GridBagLayout());
        settingsDialog.setSize(400, 400);
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Set the layout of the dialog to GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title Label
        JLabel titleLabel = new JLabel("Setting");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        settingsDialog.add(titleLabel, gbc);

        // Font for checkboxes
        Font checkBoxFont = new Font("Arial", Font.BOLD, 18);

        // Checkboxes for options
        JCheckBox showEquationCheckBox = new JCheckBox("Show Equation", true);
        showEquationCheckBox.setFocusPainted(false);
        showEquationCheckBox.setFont(checkBoxFont);
        settingsDialog.add(showEquationCheckBox, gbc);

        JCheckBox validateInputCheckBox = new JCheckBox("Validate Input", true);
        validateInputCheckBox.setFocusPainted(false);
        validateInputCheckBox.setFont(checkBoxFont);
        settingsDialog.add(validateInputCheckBox, gbc);

        JCheckBox randomSelectionCheckBox = new JCheckBox("Random Selection", true);
        randomSelectionCheckBox.setFocusPainted(false);
        randomSelectionCheckBox.setFont(checkBoxFont);
        settingsDialog.add(randomSelectionCheckBox, gbc);

        // Confirm button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font for button
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected values from the checkboxes
                boolean showEquation = showEquationCheckBox.isSelected();
                boolean validateInput = validateInputCheckBox.isSelected();
                boolean randomSelection = randomSelectionCheckBox.isSelected();
                // invoke the initializeGame method in the controller
                controller.initializeGame(showEquation, validateInput, randomSelection);
                // Reset the game interface if it is not the first run
                if (!isFirstRun) {
                    resetGameInterface();
                } else {
                    isFirstRun = false;  // Set the flag to false after the first run
                }
                // Close the settings dialog and show the main frame
                settingsDialog.dispose();
                frame.setVisible(true);
            }
        });

        settingsDialog.add(confirmButton, gbc);

        // Show the dialog
        settingsDialog.setVisible(true);
    }


    public void initializeFrame() {
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLayout(new GridBagLayout());
        //set the layout of the frame to GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        //==================set top panel================================
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel logoLabel = new JLabel(scaleImageIcon("./figure/logo.png", 150, 30));
        topPanel.add(logoLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        JButton settingsButton = createButtonWithIcon("Settings", "./figure/settings.png", 30, 30);
        settingsButton.addActionListener(e -> showPreGameSettings());

        JButton howToPlayButton = createButtonWithIcon("How to Play", "./figure/how_to_play.png", 30, 30);
        howToPlayButton.addActionListener(e -> {
            String rulesText = "<html><body style='width: 200px'>"
                    + "<h1>How to Play Numberle:</h1>"
                    + "<p><b>Goal:</b> Guess the hidden math equation in 6 tries.</p>"
                    + "<p><b>Feedback:</b> Color of the tiles changes to show how close you are.</p>"
                    + "<ul>"
                    + "<li><b>Green:</b> Correct symbol in the right position.</li>"
                    + "<li><b>Orange:</b> Correct symbol but in the wrong position.</li>"
                    + "<li><b>Grey:</b> Symbol not in the equation at all.</li>"
                    + "</ul>"
                    + "<h2>Rules:</h2>"
                    + "<ul>"
                    + "<li>Each guess must include exactly one '=' sign.</li>"
                    + "<li>Use numbers 0-9 and symbols +, -, *, /.</li>"
                    + "<li>The equation must resolve correctly (e.g., '3+2=5').</li>"
                    + "<li>Guesses must be in the format of a math equation.</li>"
                    + "<li>Each try must result in a different equation; guesses are not commutative.</li>"
                    + "<li>You have six attempts to guess the equation correctly.</li>"
                    + "</ul>"
                    + "<p>Win the game by turning all rows green!</p>"
                    + "</body></html>";

            JOptionPane.showMessageDialog(frame, rulesText, "How to Play", JOptionPane.INFORMATION_MESSAGE);
        });

        // Add the buttons to the buttonPanel
        buttonPanel.add(settingsButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(howToPlayButton);

        // Add the buttonPanel to the topPanel
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Add the topPanel to the frame
        gbc.weighty = 0.5;
        frame.add(topPanel, gbc);

        //==================set gridPanel================================
        JPanel gridPanel = new JPanel(new GridLayout(6, 1));
        Font textFieldFont = new Font("Arial", Font.PLAIN, 20);
        for (int i = 0; i < 6; i++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 7));
            for (int j = 0; j < 7; j++) {
                JTextField field = new JTextField(1);
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setFont(textFieldFont);
                inputFields[i][j] = field;
                rowPanel.add(field);
            }
            gridPanel.add(rowPanel);
        }
        gbc.insets = new Insets(15, 100, 30, 100);
        gbc.weighty = 3.5;
        frame.add(gridPanel, gbc);

        //==================set numberPanel==============================
        numberPanel = new JPanel(new GridLayout(1, 10));
        Font buttonFont = new Font("Arial", Font.BOLD, 20);
        ActionListener numberListener = createActionListener();
        String numbers = "0123456789";
        for (char c : numbers.toCharArray()) {
            JButton button = new JButton(String.valueOf(c));
            customizeButton(button, buttonFont);
            button.addActionListener(numberListener);
            button.setBackground(new Color(219, 223, 236));
            numberPanel.add(button);
        }
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0.50;
        frame.add(numberPanel, gbc);

        //=========================set operationPanel======================
        operationPanel = new JPanel(new GridLayout(1, 7));
        String[] operations = {"Delete", "+", "-", "*", "/", "=", "Enter"};
        ActionListener operationListener = createActionListener();
        for (String op : operations) {
            JButton button = new JButton(op);
            customizeButton(button, buttonFont);
            button.addActionListener(operationListener);
            button.setBackground(new Color(219, 223, 236));
            operationPanel.add(button);
        }
        gbc.weighty = 0.50;
        frame.add(operationPanel, gbc);

        // Setting the location to the center of the screen
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    // Method to reset the game interface
    private void resetGameInterface() {
        // Reset all JTextFields in inputFields
        for (JTextField[] inputField : inputFields) {
            for (JTextField jTextField : inputField) {
                jTextField.setText("");  // clear the text
                jTextField.setBackground(Color.WHITE);  // reset the background color
            }
        }

        // Reset the buttons in numberPanel and operationPanel
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton button) {
                    button.setBackground(white);  // reset the background color of the button
                    button.setEnabled(true);  // enable the button
                }
            }
        });
        // Reset the currentPanelIndex and currentInputIndex
        currentPanelIndex = 0;
        currentInputIndex = 0;

    }

    // Method to disable all buttons after the game is over
    private void disableButtons() {
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton button) {
                    button.setEnabled(false); // Disable the button
                }
            }
        });
    }

    // Method to create an ActionListener for the buttons, numbers, and operations
    private ActionListener createActionListener() {
        return e -> {
            String command = e.getActionCommand();
            // process Delete operation
            if (command.equals("Delete")) {
                if (currentInputIndex == 6 && !stop) {
                    inputFields[currentPanelIndex][currentInputIndex].setText("");
                    stop = true;
                } else if (currentInputIndex > 0) {
                    inputFields[currentPanelIndex][currentInputIndex - 1].setText("");
                    currentInputIndex--;
                }
            }
            // process Enter operation
            else if (command.equals("Enter")) {
                // collect the input from the JTextFields
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    input.append(inputFields[currentPanelIndex][i].getText());
                }
                // invoke the processInput method in the controller
                if (controller.processInput(input.toString())) {
                    if (controller.isGameOver()) {
                        disableButtons(); // Disable all buttons
                    } else if (currentPanelIndex < 5) {
                        currentPanelIndex++;
                        currentInputIndex = 0; // Start at the beginning of the next panel
                    }
                }
            }

            // process the number buttons
            else {
                if (currentInputIndex == 6) {
                    stop = false;
                }
                if (currentInputIndex < 7 && inputFields[currentPanelIndex][currentInputIndex].getText().isEmpty()) {
                    inputFields[currentPanelIndex][currentInputIndex].setText(command);
                    if (currentInputIndex < 6) {
                        currentInputIndex++;
                    }
                }
            }
        };
    }

    private static void customizeButton(JButton button, Font font) {
        button.setFocusPainted(false);
        button.setFont(font);
    }

    private static JButton createButtonWithIcon(String tooltip, String imagePath, int width, int height) {
        JButton button = new JButton(scaleImageIcon(imagePath, width, height));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(new Color(236, 236, 237));
        return button;
    }

    private static ImageIcon scaleImageIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    // Method to apply feedback colors to the JTextFields
    private void applyFeedbackColors(String feedback) {
        if (feedback == null || feedback.length() != 7) {
            JOptionPane.showMessageDialog(frame, "Invalid feedback length.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < feedback.length(); i++) {
            char ch = feedback.charAt(i);
            Color colorToApply = switch (ch) {
                case 'G' -> green;
                case 'O' -> orange;
                case 'X' -> grey;
                default -> throw new IllegalArgumentException("Unexpected value: " + ch);
            };

            JTextField field = inputFields[currentPanelIndex][i];
            field.setBackground(colorToApply);
        }
    }

    // Method to update the colors of the buttons based on the feedback, correctPositions, wrongPositions, notInEquation, and unused
    private void updateButtonColors(Set<Character> correctPositions, Set<Character> wrongPositions, Set<Character> notInEquation, Set<Character> unused) {
        // Iterate over all buttons in numberPanel and operationPanel
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton button) {
                    char ch = button.getText().charAt(0);

                    Color currentColor = button.getBackground();

                    // if the button is already green or grey, skip it
                    if (currentColor.equals(green) || currentColor.equals(grey)) {
                        continue;
                    }

                    // Apply the appropriate color based on the character
                    if (correctPositions.contains(ch)) {
                        button.setBackground(green);
                    } else if (notInEquation.contains(ch) && !currentColor.equals(green)) {
                        button.setBackground(grey);
                    } else if (wrongPositions.contains(ch) && !currentColor.equals(green) && !currentColor.equals(grey)) {
                        button.setBackground(orange);
                    } else if (unused.contains(ch)) {
                        button.setBackground(white);
                    } else {
                        // Reset the color to white if the character is not in any of the sets
                        button.setBackground(white);
                    }
                }
            }
        });
    }

    // Method to update the view based on the changes in the model
    @Override
    public void update(java.util.Observable o, Object arg) {
        // Enable the hintButton if the displayTargetEquation is true
        hintButton.setEnabled(controller.getDisplayTargetEquation());

        if (arg instanceof String message) {
            switch (message) {
                case "CheckGameFailed" -> {
                    if (!controller.isGameWon() && controller.isGameOver()) {
                        JOptionPane.showMessageDialog(frame, "Unfortunately, you did not guess the target equation correctly. The target equation is:" + controller.getTargetWord());
                    }
                }
                case "Restart" -> {
                    if (controller.getRemainingAttempts() <= 5) {
                        restartButton.setEnabled(true);
                    }
                }
                case "Feedback" -> {
                    String feedback = controller.getFeedback();
                    applyFeedbackColors(feedback);
                }
                case "UpdateKeyboard" -> {
                    Set<Character> correctPositions = controller.getCorrectPositions();
                    Set<Character> wrongPositions = controller.getWrongPositions();
                    Set<Character> notInEquation = controller.getNotInEquation();
                    Set<Character> unused = controller.getUnused();
                    updateButtonColors(correctPositions, wrongPositions, notInEquation, unused);
                }
                // Display the message in a dialog box for error messages and won information
                default -> JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
