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
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField[][] inputFields = new JTextField[6][7];
    private final Color green = new Color(61, 191, 165);   // 绿色，代表正确位置
    private final Color orange = new Color(241, 155, 113); // 橙色，代表错误位置
    private final Color grey = new Color(165, 172, 195);   // 灰色，代表不在等式中
    private final Color white = new Color(219, 223, 236);  // 白色，代表未使用

    private final JButton hintButton; // Declare hintButton at class level
    private final JButton restartButton; // Declare restartButton at class level

    private JPanel numberPanel;
    private JPanel operationPanel;


    private boolean isFirstRun = true;  // 初始设置为 true，表示第一次点击

    boolean stop = false;

    private int currentPanelIndex = 0;
    private int currentInputIndex = 0;

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;

        hintButton = createButtonWithIcon("Hint", "./figure/hint.png", 30, 30);
        hintButton.addActionListener(e -> {
            String targetEquation = controller.getTargetWord();
            JOptionPane.showMessageDialog(frame, "Target Equation: " + targetEquation, "Hint", JOptionPane.INFORMATION_MESSAGE);
        });

        restartButton = createButtonWithIcon("Restart", "./figure/restart.png", 30, 30);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            restartButton.setEnabled(false);
            resetGameInterface();
            controller.restartGame();

        });


        ((NumberleModel) this.model).addObserver(this);
        this.controller.setView(this);
        update((NumberleModel) this.model, null);
        showPreGameSettings();
        initializeFrame();
    }

    private void showPreGameSettings() {
        JDialog settingsDialog = new JDialog(frame, "Settings", true);
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setLayout(new GridBagLayout());
        settingsDialog.setSize(400, 400);
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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
                // 获取复选框的选中状态
                boolean showEquation = showEquationCheckBox.isSelected();
                boolean validateInput = validateInputCheckBox.isSelected();
                boolean randomSelection = randomSelectionCheckBox.isSelected();
                // 调用controller的方法，传递设置信息
                controller.initializeGame(showEquation, validateInput, randomSelection);
                // 如果不是第一次点击，执行重置界面函数
                if (!isFirstRun) {
                    resetGameInterface();
                } else {
                    isFirstRun = false;  // 更新标志，表示首次点击已完成
                }
                // 关闭对话框并显示主游戏窗口
                settingsDialog.dispose();
                frame.setVisible(true);
            }
        });

        settingsDialog.add(confirmButton, gbc);

        // Show the dialog
        settingsDialog.setVisible(true);
    }


    public void initializeFrame() {
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
            JOptionPane.showMessageDialog(frame, "Game Rules:\n[Insert game rules here]", "How to Play", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(settingsButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(howToPlayButton);

        topPanel.add(buttonPanel, BorderLayout.EAST);

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

    private void resetGameInterface() {
        // Reset all JTextFields in inputFields
        for (JTextField[] inputField : inputFields) {
            for (JTextField jTextField : inputField) {
                jTextField.setText("");  // 清空文本
                jTextField.setBackground(Color.WHITE);  // 重置背景色为默认
            }
        }

        // Reset the buttons in numberPanel and operationPanel
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton button) {
                    button.setBackground(white);  // 清除按钮背景色
                    button.setEnabled(true);  // 重新启用按钮
                }
            }
        });

        currentPanelIndex = 0;
        currentInputIndex = 0;

    }


    private void disableButtons() {
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    button.setEnabled(false); // 禁用按钮
                }
            }
        });
    }

    private ActionListener createActionListener() {
        return e -> {
            String command = e.getActionCommand();
            // 处理删除操作
            if (command.equals("Delete")) {
                if (currentInputIndex == 6 && !stop) {
                    inputFields[currentPanelIndex][currentInputIndex].setText("");
                    stop = true;
                } else if (currentInputIndex > 0) {
                    inputFields[currentPanelIndex][currentInputIndex - 1].setText("");
                    currentInputIndex--;
                }
            }
            // 处理 Enter 操作
            else if (command.equals("Enter")) {
                // 收集当前行的输入
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    input.append(inputFields[currentPanelIndex][i].getText());
                }
                // 调用 controller 的 processInput 方法
                if (controller.processInput(input.toString())) {
                    if (controller.isGameOver()) {
                        disableButtons(); // 禁用所有按钮的方法
                    } else if (currentPanelIndex < 5) {
                        currentPanelIndex++;
                        currentInputIndex = 0; // Start at the beginning of the next panel
                    }
                }
            }

            // 处理数字和运算符输入
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

    private void updateButtonColors(Set<Character> correctPositions, Set<Character> wrongPositions, Set<Character> notInEquation, Set<Character> unused) {
        // 将逻辑应用于两个面板
        Stream.of(numberPanel, operationPanel).forEach(panel -> {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JButton button) {
                    char ch = button.getText().charAt(0);

                    Color currentColor = button.getBackground();

                    // 如果按钮已经是绿色或灰色，则跳过更新
                    if (currentColor.equals(green) || currentColor.equals(grey)) {
                        continue;
                    }

                    // 根据所属集合更新颜色
                    if (correctPositions.contains(ch)) {
                        button.setBackground(green);
                    } else if (notInEquation.contains(ch) && !currentColor.equals(green)) {
                        button.setBackground(grey);
                    } else if (wrongPositions.contains(ch) && !currentColor.equals(green) && !currentColor.equals(grey)) {
                        button.setBackground(orange);
                    } else if (unused.contains(ch)) {
                        button.setBackground(white);
                    } else {
                        // 如果字符不在任何集合中，重置为默认颜色
                        button.setBackground(white);
                    }
                }
            }
        });
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
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
                default -> JOptionPane.showMessageDialog(frame, message);
            }
        }
    }
}
