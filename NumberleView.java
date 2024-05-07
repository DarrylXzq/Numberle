// NumberleView.java
import javax.swing.*;
import java.awt.*;
import java.util.Observer;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);;
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);
        frame.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));

        inputPanel.add(inputTextField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
            inputTextField.setText("");
        });
        inputPanel.add(submitButton);

        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        inputPanel.add(attemptsLabel);
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.X_AXIS));
        keyboardPanel.add(new JPanel());
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(2, 5));
        keyboardPanel.add(numberPanel);

        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setEnabled(true);
            button.addActionListener(e -> {
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
        }

        keyboardPanel.add(new JPanel());

        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
    }
}