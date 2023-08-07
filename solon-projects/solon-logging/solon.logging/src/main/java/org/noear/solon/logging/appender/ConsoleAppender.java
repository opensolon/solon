package org.noear.solon.logging.appender;

import org.noear.solon.Solon;
import org.noear.solon.logging.event.Level;

import java.io.Console;

/**
 * 控制台添加器实现类（限制在调试模式或文件模式下打印）
 *
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        Console console = System.console();

        if (console != null) {
            setOutput(console.writer());
        } else {
            setOutput(System.out);
        }
    }
}
