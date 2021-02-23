package org.noear.logging;

import org.noear.mlog.Appender;
import org.noear.mlog.Level;
import org.noear.mlog.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public interface LogAppender extends Appender {
    Level getLevel();

    void setLevel(Level level);

    boolean getEnable();

    default Level getDefaultLevel() {
        return LoggerFactory.getLevel();
    }
}
