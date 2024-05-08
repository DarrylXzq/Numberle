import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        // 创建 Scanner 对象
        Scanner scanner = new Scanner(System.in);
        // 创建 Numberle 模型
        INumberleModel model = new NumberleModel();

        // 从用户读取配置参数
        int showEquation = model.readBinaryInput(scanner, "是否显示目标方程式 (0=否, 1=是): ");
        int validateInput = model.readBinaryInput(scanner, "是否验证输入有效性 (0=否, 1=是): ");
        int randomSelection = model.readBinaryInput(scanner, "是否使用随机方程式 (0=否, 1=是): ");

        // 初始化模型
        model.initialize(model, showEquation, validateInput, randomSelection);

        System.out.println("欢迎来到 Numberle 游戏！你有 " + NumberleModel.MAX_ATTEMPTS + " 次机会猜测正确的数学方程式。");
        model.gameLogic(model);

        scanner.close();
    }
}
