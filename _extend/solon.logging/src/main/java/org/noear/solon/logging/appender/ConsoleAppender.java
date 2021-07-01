package org.noear.solon.logging.appender;

import org.noear.solon.logging.event.Level;

/**
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        if (System.console() != null) {
            init(System.out);
        }
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }
}
