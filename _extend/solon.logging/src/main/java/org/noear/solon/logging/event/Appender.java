package org.noear.solon.logging.event;

import org.noear.solon.logging.LogOptions;

/**
 * 日志添加器
 *
 * @author noear
 * @since 1.0
 */
public interface Appender{
    default Level getDefaultLevel() {
        return LogOptions.getLevel();
    }

    default void start(){}

    String getName();
    void setName(String name);

    void append(LogEvent logEvent);
}
