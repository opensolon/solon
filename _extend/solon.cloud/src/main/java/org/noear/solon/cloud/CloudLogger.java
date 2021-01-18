package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Level;

/**
 * @author noear 2021/1/17 created
 */
public interface CloudLogger {
    static CloudLogger get(String name) {
        if (CloudClient.log() == null) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(name);
        }
    }

    static CloudLogger get(String name, Class<?> clz) {
        if (CloudClient.log() == null) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(name, clz);
        }
    }


    String getName();

    void setName(String name);

    default boolean isTraceEnabled() {
        return true;
    }

    void trace(Object content);

    void trace(String summary, Object content);

    void trace(String tag1, String summary, Object content);

    void trace(String tag1, String tag2, String summary, Object content);

    void trace(String tag1, String tag2, String tag3, String summary, Object content);

    void trace(String tag1, String tag2, String tag3, String tag4, String summary, Object content);

    default boolean isDebugEnabled() {
        return true;
    }

    void debug(Object content);

    void debug(String summary, Object content);

    void debug(String tag1, String summary, Object content);

    void debug(String tag1, String tag2, String summary, Object content);

    void debug(String tag1, String tag2, String tag3, String summary, Object content);

    void debug(String tag1, String tag2, String tag3, String tag4, String summary, Object content);

    default boolean isInfoEnabled() {
        return true;
    }

    void info(Object content);

    void info(String summary, Object content);

    void info(String tag1, String summary, Object content);

    void info(String tag1, String tag2, String summary, Object content);

    void info(String tag1, String tag2, String tag3, String summary, Object content);

    void info(String tag1, String tag2, String tag3, String tag4, String summary, Object content);

    default boolean isWarnEnabled() {
        return true;
    }

    void warn(Object content);

    void warn(String summary, Object content);

    void warn(String tag1, String summary, Object content);

    void warn(String tag1, String tag2, String summary, Object content);

    void warn(String tag1, String tag2, String tag3, String summary, Object content);

    void warn(String tag1, String tag2, String tag3, String tag4, String summary, Object content);

    default boolean isErrorEnabled() {
        return true;
    }

    void error(Object content);

    void error(String summary, Object content);

    void error(String tag1, String summary, Object content);

    void error(String tag1, String tag2, String summary, Object content);

    void error(String tag1, String tag2, String tag3, String summary, Object content);

    void error(String tag1, String tag2, String tag3, String tag4, String summary, Object content);


    void write(Level level, Object content);

    void write(Level level, String summary, Object content);

    void write(Level level, String tag1, String summary, Object content);

    void write(Level level, String tag1, String tag2, String summary, Object content);

    void write(Level level, String tag1, String tag2, String tag3, String summary, Object content);

    void write(Level level, String tag1, String tag2, String tag3, String tag4, String summary, Object content);
}
