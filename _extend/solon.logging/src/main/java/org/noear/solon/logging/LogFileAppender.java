package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;

/**
 * file appender
 *
 * @author noear
 * @since 1.3
 */
public class LogFileAppender extends LogPrintStreamAppender {
    @Override
    public Level getDefaultLevel() {
        return Level.INFO;
    }

}
