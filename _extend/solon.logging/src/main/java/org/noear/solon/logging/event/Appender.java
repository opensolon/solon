package org.noear.solon.logging.event;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.logging.LogOptions;

/**
 * 日志添加器
 *
 * @author noear
 * @since 1.0
 */
public interface Appender extends Lifecycle {
    default Level getDefaultLevel() {
        return LogOptions.getLevel();
    }

    String getName();
    void setName(String name);

    void append(LogEvent logEvent);
}
