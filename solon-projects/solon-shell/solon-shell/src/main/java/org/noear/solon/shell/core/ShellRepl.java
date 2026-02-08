/*
 * Copyright 2017-2026 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.shell.core;

import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.shell.integration.ShellContext;

import java.util.Locale;
import java.util.Scanner;

/**
 * Solon Shell 对外启动类，实现 repl 核心
 *
 * @author noear
 * @author shenmk
 * @since 3.9.1
 */
@Deprecated
public class ShellRepl {

    /**
     * 启动交互式终端
     */
    public static void start() {
        // 1. 初始化 Scanner，接收终端输入
        Scanner scanner = new Scanner(System.in);
        String prompt = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.prompt"); // 提示符

        // 2. 打印欢迎信息
        System.out.println("=====================================");
        System.out.println("Solon Shell start success");
        System.out.println(I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.help.welcome"));
        System.out.println("=====================================");

        // 3. 终端循环（直到输入 exit 退出）
        while (true) {
            try {
                // 打印提示符，读取用户输入
                System.out.print(prompt + " ");
                String commandLine = scanner.nextLine().trim();

                // 处理退出命令
                if (commandLine.equalsIgnoreCase("exit")) {
                    System.out.println("bye！");
                    break;
                }

                // 执行命令，输出结果
                String result = CommandExecutor.execute(commandLine);
                System.out.println(result);
            } catch (Exception e) {
                System.err.println(" command error ：" + e.getMessage());
            }
        }

        scanner.close();
    }
}