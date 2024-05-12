public class TestAssertions {
    public static void main(String[] args) {
        // 故意设置一个必定失败的断言
        assert false : "断言已启用！";

        // 如果执行到这里，说明断言没有启用
        System.out.println("断言没有启用。");
    }
}

