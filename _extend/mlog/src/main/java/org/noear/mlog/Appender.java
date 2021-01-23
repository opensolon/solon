package org.noear.mlog;

/**
 * @author noear
 * @since 1.2
 */
public interface Appender {
    void append(Level level, Marker meta, Object content);
}
