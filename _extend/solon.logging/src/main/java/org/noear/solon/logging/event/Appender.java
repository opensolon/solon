package org.noear.solon.logging.event;

/**
 * 日志添加器
 *
 * @author noear
 * @since 1.0
 */
public interface Appender {
    String getName();

    void append(LogEvent logEvent);
}
