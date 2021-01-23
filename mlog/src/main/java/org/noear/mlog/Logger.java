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
    void trace(Metainfo metainfo, Object content);
    void trace(Metainfo metainfo, String format, Object... args);



    default boolean isDebugEnabled() {
        return LoggerFactory.getLevel().code <= Level.DEBUG.code;
    }

    void debug(Object content);
    void debug(String format, Object... args);
    void debug(Metainfo metainfo, Object content);
    void debug(Metainfo metainfo, String format, Object... args);



    default boolean isInfoEnabled() {
        return LoggerFactory.getLevel().code <= Level.INFO.code;
    }

    void info(Object content);
    void info(String format, Object... args);
    void info(Metainfo metainfo, Object content);
    void info(Metainfo metainfo, String format, Object... args);



    default boolean isWarnEnabled() {
        return LoggerFactory.getLevel().code <= Level.WARN.code;
    }

    void warn(Object content);
    void warn(String format, Object... args);
    void warn(Metainfo metainfo, Object content);
    void warn(Metainfo metainfo, String format, Object... args);


    default boolean isErrorEnabled() {
        return LoggerFactory.getLevel().code <= Level.ERROR.code;
    }

    void error(Object content);
    void error(String format, Object... args);
    void error(Metainfo metainfo, Object content);
    void error(Metainfo metainfo, String format, Object... args);
}
