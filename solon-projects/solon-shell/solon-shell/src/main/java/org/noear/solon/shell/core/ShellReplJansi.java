/*
 * Copyright 2024 noear.org
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

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.shell.integration.ShellContext;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Solon Shell 对外启动类，集成 JLine + Jansi 增强终端交互（极简默认配置版）
 *
 * @author shenmk
 * @author ZhangSan
 * @since 3.9.1
 */
public class ShellReplJansi {

    public static void start() {
        // 仅做基础Jansi初始化（默认配置）
        AnsiConsole.systemInstall();

        try {
            // 核心：使用JLine默认逻辑构建Terminal，仅保留必要的Jansi启用
            Terminal terminal = TerminalBuilder.builder()
                    .jansi(true) // 仅启用Jansi，其余均为默认
                    .build();

            // LineReader仅保留核心功能，全部使用默认配置
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter(getAllCommandNames()))
                    .history(new DefaultHistory())
                    .build();

            // 彩色欢迎信息（保留核心文案，无额外配置）
            System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(getI18nMessage("shell.welcome", "欢迎使用 Solon Shell 终端")).reset());
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(getI18nMessage("shell.start.success", "Shell 终端启动成功")).reset());
            System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(getI18nMessage("shell.help.tip", "提示：输入 help 查看所有命令，exit 退出终端")).reset());

            // 彩色提示符（默认样式）
            String prompt = Ansi.ansi().fg(Ansi.Color.BLUE).bold().a(getI18nMessage("shell.prompt", "solon> ")).reset().toString();

            // 终端循环（核心逻辑保留，无额外配置）
            while (true) {
                String commandLine;
                try {
                    commandLine = reader.readLine(prompt + " ").trim();

                    if (commandLine.equalsIgnoreCase("exit")) {
                        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(getI18nMessage("shell.exit.bye", "感谢使用，再见！")).reset());
                        break;
                    }
                    String result = CommandExecutor.execute(commandLine);
                    if (result.startsWith("【错误】")) {
                        System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(result).reset());
                    } else {
                        System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(result).reset());
                    }
                } catch (UserInterruptException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(getI18nMessage("shell.interrupt.tip", "\n【提示】按 exit 退出终端，Ctrl+C 仅中断当前输入")).reset());
                } catch (EndOfFileException e) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(getI18nMessage("shell.exit.bye", "感谢使用，再见！")).reset());
                    break;
                } catch (Exception e) {
                    System.err.println(Ansi.ansi().fg(Ansi.Color.RED).a(getI18nMessage("shell.error.terminal", "终端执行异常：") + e.getMessage()).reset());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("初始化终端失败", e);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    /**
     * 获取国际化消息（极简逻辑，仅做基础兼容）
     */
    private static String getI18nMessage(String key, String defaultValue) {
        try {
            String message = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, key);
            return (message == null || message.trim().isEmpty()) ? defaultValue : message;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取所有注册的命令名（默认逻辑）
     */
    private static Set<String> getAllCommandNames() {
        return ShellContext.getCommandRepository().keySet()
                .stream()
                .collect(Collectors.toSet());
    }
}