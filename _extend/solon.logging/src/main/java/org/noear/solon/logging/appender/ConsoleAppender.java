package org.noear.solon.logging.appender;

import org.noear.solon.Solon;
import org.noear.solon.logging.event.Level;

/**
 * 控制台添加器实现类（限制在调试模式或文件模式下打印）
 *
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        setStream(System.out);
    }

    /**
     * 获取默认级别
     */
    @Override
    public Level getDefaultLevel() {
        if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
            return Level.TRACE;
        } else {
            return Level.INFO;
        }
    }
}
