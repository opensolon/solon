package org.noear.mlog;

/**
 * 日志器
 *
 * @author noear
 * @since 1.2
 */
public interface Logger {
    String getName();

    void setName(String name);



    default boolean isTraceEnabled() {
        return LoggerFactory.getLevel().code <= Level.TRACE.code;
    }


    void trace(Object content);
    void trace(String format, Object... args);
    void trace(Marker marker, Object content);
    void trace(Marker marker, String format, Object... args);



    default boolean isDebugEnabled() {
        return LoggerFactory.getLevel().code <= Level.DEBUG.code;
    }

    void debug(Object content);
    void debug(String format, Object... args);
    void debug(Marker marker, Object content);
    void debug(Marker marker, String format, Object... args);



    default boolean isInfoEnabled() {
        return LoggerFactory.getLevel().code <= Level.INFO.code;
    }

    void info(Object content);
    void info(String format, Object... args);
    void info(Marker marker, Object content);
    void info(Marker marker, String format, Object... args);



    default boolean isWarnEnabled() {
        return LoggerFactory.getLevel().code <= Level.WARN.code;
    }

    void warn(Object content);
    void warn(String format, Object... args);
    void warn(Marker marker, Object content);
    void warn(Marker marker, String format, Object... args);


    default boolean isErrorEnabled() {
        return LoggerFactory.getLevel().code <= Level.ERROR.code;
    }

    void error(Object content);
    void error(String format, Object... args);
    void error(Marker marker, Object content);
    void error(Marker marker, String format, Object... args);
}
