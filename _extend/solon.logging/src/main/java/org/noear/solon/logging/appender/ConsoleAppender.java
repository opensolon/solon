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

    @Override
    protected boolean getEnable() {
        return Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode();
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }
}
