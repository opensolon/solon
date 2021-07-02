package org.noear.solon.logging.appender;

import org.noear.solon.Solon;
import org.noear.solon.logging.event.Level;

/**
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
            setStream(System.out);
        }
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }
}
