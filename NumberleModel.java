import java.util.*;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class NumberleModel extends Observable implements INumberleModel {

    private String targetEquation;
    private StringBuilder currentGuess;
//    ColoredChar[] currentGuess;

    private int remainingAttempts;
    private boolean gameWon;

    private boolean displayErrorIfInvalid;
    private boolean displayTargetEquation;
    private boolean useRandomSelection;

    private List<String> validEquations;

    // 正则表达式, 用于验证等式的有效性
    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[0-9\\+\\-\\*/%=]*$");
    private static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("=");
    private static final Pattern ARITHMETIC_OPERATORS_PATTERN = Pattern.compile(".*[\\+\\-\\*/].");
    private static final Pattern OPERATOR_SEQUENCE_PATTERN = Pattern.compile("[\\+\\-\\*/]{2,}");

    private final List<String> ERROR_MESSAGES = List.of(
            "方程式包含非法字符。",
            "方程式太短，必须至少7个字符。",
            "方程式太长，必须不超过7个字符。",
            "方程式必须包含一个等号。",
            "方程式缺少运算符。",
            "方程式中的运算符连续出现。",
            "等式两边的计算结果不相等。"
    );

    private List<Integer> errorIndices = new ArrayList<>();

    @Override
    public int readBinaryInput(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input == 0 || input == 1) {
                    scanner.nextLine(); // 清空 Scanner 缓存，防止后续读取出错
                    return input;
                }
            } else {
                scanner.next(); // 清空非整数输入
            }
            System.out.println("无效输入，请输入 0 或 1。");
        }
    }


    private void configureModel(INumberleModel model, int showEquation, int validateInput, int randomSelection) {
        model.setDisplayTargetEquation(showEquation == 1);
        model.setDisplayErrorIfInvalid(validateInput == 1);
        model.setUseRandomSelection(randomSelection == 1);
    }

    @Override
    public void initialize(INumberleModel model, int showEquation, int validateInput, int randomSelection) {
        loadValidEquations();
        configureModel(model, showEquation, validateInput, randomSelection);
        startNewGame();
    }

    private void displayTargetEquation() {
        if (displayTargetEquation) {
            System.out.println("目标方程式为：" + getTargetEquation());
        }
    }

    @Override
    public void gameLogic(INumberleModel model) {
        Scanner scanner = new Scanner(System.in);
        displayTargetEquation();
        // 游戏主循环
        while (!model.isGameOver()) {
            System.out.print("\n请输入方程式 (7 个字符，包括一个等号)：");
            String input = scanner.nextLine();

            // 处理输入
            if (!model.processInput(input)) {
                for (int index : errorIndices) {
                    System.out.println(ERROR_MESSAGES.get(index));
                }
                displayTargetEquation();
                continue;
            }

            // 检查是否赢得比赛
            if (model.isGameWon()) {
                System.out.println("\n恭喜你猜对了方程式！");
                break;
            }
            //显示目标方程式
            displayTargetEquation();
            // 显示当前猜测状态和剩余次数
            System.out.println("当前猜测：" + model.getCurrentGuess());
            System.out.println("剩余尝试次数：" + model.getRemainingAttempts());
        }

        // 显示最终结果
        if (!model.isGameWon()) {
            System.out.println("\n很遗憾，你未能猜中目标方程式。目标方程式是：" + model.getTargetEquation());
        }

        System.out.println("\n游戏结束，谢谢参与！");
        scanner.close();
    }

    @Override
    public boolean processInput(String input) {
        // 首先判断是否需要显示错误信息
        if (displayErrorIfInvalid) {
            // 验证用户输入
            if (!isValidEquation(input)) {
                return false;  // 直接返回，不减少尝试次数
            }
        }

        // 检查输入的等式是否匹配目标
        remainingAttempts--;  // 有效尝试，减少一次尝试次数
        if (input.equals(targetEquation)) {
            gameWon = true;
        } else {
            // 为玩家的猜测提供反馈
            currentGuess.setLength(0);
            currentGuess.append(evaluateFeedback(input));
        }
        setChanged();
        notifyObservers();
        return true;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
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
    public void startNewGame() {
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;
        currentGuess = new StringBuilder("       ");

        if (useRandomSelection) {
            Random rand = new Random();
            targetEquation = validEquations.get(rand.nextInt(validEquations.size()));
        } else {
            targetEquation = validEquations.get(0); // 固定的等式（例如文件中的第一个）
        }

        setChanged();
        notifyObservers();
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

    private void loadValidEquations() {
        // 从文件中加载等式
        try {
            validEquations = Files.readAllLines(Paths.get("equations.txt"));
        } catch (IOException e) {
            System.err.println("读取等式文件时出错：" + e.getMessage());
            validEquations = new ArrayList<>();
        }
    }

    private boolean isValidEquation(String equation) {
        errorIndices.clear(); // 清空之前的错误索引
        // 第一步: 检查是否包含除0-9和+-*%=之外的字符，可以为空
        if (!VALID_CHARS_PATTERN.matcher(equation).matches()) {
            errorIndices.add(0);
            return false;
        }

        // 第二步: 判断输入的字符是否正确长度
        if (equation.length() < 7) {
            errorIndices.add(1);
            return false;
        } else if (equation.length() > 7) {
            errorIndices.add(2);
            return false;
        }

        // 第三步: 判断等式中是否有等号
        if (!EQUAL_SIGN_PATTERN.matcher(equation).find()) {
            errorIndices.add(3);
            return false;
        }

        // 第四步: 判断等式中是否至少包含一个+-*%
        if (!ARITHMETIC_OPERATORS_PATTERN.matcher(equation).find()) {
            errorIndices.add(4);
            return false;
        }

        // 第五步: 检查运算符是否有连续出现
        if (OPERATOR_SEQUENCE_PATTERN.matcher(equation).find()) {
            errorIndices.add(5);
            return false;
        }

        // 第六步: 检查等式两边的计算结果是否相等
        try {
            String[] parts = equation.split("=");
            if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                errorIndices.add(6);
                return false;
            }
            double leftResult = evaluateExpression(parts[0]);
            double rightResult = evaluateExpression(parts[1]);
            if (Math.abs(leftResult - rightResult) > 0.0001) { // 使用一定的容差来比较浮点数结果
                errorIndices.add(6);
                return false;
            }
        } catch (Exception e) {
            errorIndices.add(6);
            return false;
        }
        return true;
    }

    // BODMAS 运算逻辑实现
    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int index = 0;
        while (index < expression.length()) {
            char c = expression.charAt(index);

            // 处理数字
            if (Character.isDigit(c)) {
                StringBuilder numberBuilder = new StringBuilder();
                while (index < expression.length() && Character.isDigit(expression.charAt(index))) {
                    numberBuilder.append(expression.charAt(index++));
                }
                numbers.push(Double.parseDouble(numberBuilder.toString()));
                continue; // 跳过递增，以便准确处理运算符
            }

            // 处理运算符并根据 BODMAS 原则进行操作
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }

            index++;
        }

        // 处理剩余的操作符
        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char currentOp, char stackOp) {
        // 判断当前操作符的优先级
        if ((currentOp == '*' || currentOp == '/') && (stackOp == '+' || stackOp == '-')) {
            return false;
        }
        return true;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            default:
                throw new UnsupportedOperationException("无效的操作符：" + operator);
        }
    }

    private String evaluateFeedback(String input) {
        // 使用颜色指示反馈
        StringBuilder feedback = new StringBuilder("       ");
        // 将输入和目标转化为字符数组
        char[] inputChars = input.toCharArray();
        char[] targetChars = targetEquation.toCharArray();

        // 首先标记完全匹配的字符
        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == targetChars[i]) {
                feedback.setCharAt(i, 'G'); // 绿色表示完全匹配
                targetChars[i] = ' '; // 标记为已用
            }
        }

        // 标记部分匹配的字符
        for (int i = 0; i < inputChars.length; i++) {
            if (feedback.charAt(i) == 'G') continue; // 跳过完全匹配的字符
            for (int j = 0; j < targetChars.length; j++) {
                if (inputChars[i] == targetChars[j] && targetChars[j] != ' ') {
                    feedback.setCharAt(i, 'O'); // 橙色表示位置错误
                    targetChars[j] = ' '; // 标记为已用
                    break;
                }
            }
        }

        // 任何剩余的字符都是不正确的（灰色）
        for (int i = 0; i < feedback.length(); i++) {
            if (feedback.charAt(i) != 'G' && feedback.charAt(i) != 'O') {
                feedback.setCharAt(i, 'X'); // 灰色表示不正确
            }
        }

        return feedback.toString();
    }

    private void updateCurrentGuess(String input) {
        String feedback = evaluateFeedback(input);
        for (int i = 0; i < feedback.length(); i++) {
            char feedbackChar = feedback.charAt(i);
            String color = switch (feedbackChar) {
                case 'G' -> "\033[32m";  // Green
                case 'O' -> "\033[33m";  // Orange
                default -> "\033[31m";  // Red (assume 'X' and any other case)
            };
            currentGuess[i] = new ColoredChar(input.charAt(i), color);
        }
    }

}
