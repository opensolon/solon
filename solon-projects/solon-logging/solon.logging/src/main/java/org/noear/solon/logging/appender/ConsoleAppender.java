package org.noear.solon.logging.appender;

import java.io.Console;
import java.io.PrintWriter;

/**
 * 控制台添加器实现类（限制在调试模式或文件模式下打印）
 *
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        Console console = System.console();

        if (console != null && console.writer() != null) {
            setOutput(console.writer());
        } else {
            setOutput(new PrintWriter(System.out, true));
        }
    }
}
