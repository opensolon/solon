package org.noear.mlog;

/**
 * 云日志器
 *
 * @author noear
 * @since 1.2
 */
public interface Logger {
    String getName();

    void setName(String name);



    default boolean isTraceEnabled() {
        return true;
    }

    void trace(Object content);
    void trace(String format, Object[] args);
    void trace(Marker marker, Object content);
    void trace(Marker marker, String format, Object[] args);



    default boolean isDebugEnabled() {
        return true;
    }

    void debug(Object content);
    void debug(String format, Object[] args);
    void debug(Marker marker, Object content);
    void debug(Marker marker, String format, Object[] args);



    default boolean isInfoEnabled() {
        return true;
    }

    void info(Object content);
    void info(String format, Object[] args);
    void info(Marker marker, Object content);
    void info(Marker marker, String format, Object[] args);



    default boolean isWarnEnabled() {
        return true;
    }

    void warn(Object content);
    void warn(String format, Object[] args);
    void warn(Marker marker, Object content);
    void warn(Marker marker, String format, Object[] args);


    default boolean isErrorEnabled() {
        return true;
    }

    void error(Object content);
    void error(String format, Object[] args);
    void error(Marker marker, Object content);
    void error(Marker marker, String format, Object[] args);
}
