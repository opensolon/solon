package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;

/**
 * @author noear
 * @since 1.3
 */
public class ConsoleAppender extends OutputStreamAppender {
    public ConsoleAppender() {
        super(System.out);
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }
}
