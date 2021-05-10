package org.noear.solon.logging;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.Level;

/**
 * @author noear
 * @since 1.3
 */
public interface LogAppender extends Appender , Lifecycle {
    void setName(String name);

    Level getLevel();

    void setLevel(Level level);

    boolean getEnable();

    default Level getDefaultLevel() {
        return LogOptions.getLevel();
    }
}
