import java.security.spec.RSAOtherPrimeInfo;
import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        System.out.println("  _   _                       _                     _             ____   _       ___ \n" +
                " | \\ | |  _   _   _ __ ___   | |__     ___   _ __  | |   ___     / ___| | |     |_ _|\n" +
                " |  \\| | | | | | | '_ ` _ \\  | '_ \\   / _ \\ | '__| | |  / _ \\   | |     | |      | | \n" +
                " | |\\  | | |_| | | | | | | | | |_) | |  __/ | |    | | |  __/   | |___  | |___   | | \n" +
                " |_| \\_|  \\__,_| |_| |_| |_| |_.__/   \\___| |_|    |_|  \\___|    \\____| |_____| |___|\n" +
                "                                                                                     ");
        // 创建 Scanner 对象
        Scanner scanner = new Scanner(System.in);
        // 创建 Numberle 模型
        INumberleModel model = new NumberleModel();

        // 从用户读取配置参数
        int showEquation = model.readBinaryInput(scanner, "🚩是否显示目标方程式 (0=否, 1=是): ");
        int validateInput = model.readBinaryInput(scanner, "🚩是否验证输入有效性 (0=否, 1=是): ");
        int randomSelection = model.readBinaryInput(scanner, "🚩是否使用随机方程式 (0=否, 1=是): ");

        // 初始化模型
        model.initialize(model, showEquation, validateInput, randomSelection);
        System.out.println("\n✨✨欢迎来到 Numberle 游戏！你有 " + NumberleModel.MAX_ATTEMPTS + " 次机会猜测正确的数学方程式。✨✨");
        model.gameLogic(model);
        scanner.close();
    }
}

/**
 * 1.不能循环进行游戏
 * 2.当不检测输入有效性时，长度和特殊符号会出问题
 */