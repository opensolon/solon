package org.noear.solon.shell.core;

import java.util.Scanner;

/**
 * Solon Shell 对外启动类（）
 */
public class SolonShell {
    /**
     * 启动交互式终端
     */
    public static void start() {
        // 1. 初始化 Scanner，接收终端输入
        Scanner scanner = new Scanner(System.in);
        String prompt = "solon> "; // 提示符

        // 2. 打印欢迎信息
        System.out.println("=====================================");
        System.out.println("Solon Shell 启动成功");
        System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出");
        System.out.println("=====================================");

        // 3. 终端循环（直到输入 exit 退出）
        while (true) {
            try {
                // 打印提示符，读取用户输入
                System.out.print(prompt);
                String commandLine = scanner.nextLine().trim();

                // 处理退出命令
                if (commandLine.equalsIgnoreCase("exit")) {
                    System.out.println("再见！");
                    break;
                }

                // 执行命令，输出结果
                String result = CommandExecutor.execute(commandLine);
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("【终端异常】：" + e.getMessage());
            }
        }

        scanner.close();
    }
}