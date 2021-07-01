package org.noear.solon.logging.appender;

import org.noear.solon.Solon;
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
    public boolean getEnable() {

        return Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode();
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }
}
