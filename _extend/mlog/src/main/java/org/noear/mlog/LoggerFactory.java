package org.noear.mlog;


/**
 * @author noear
 * @since 1.2
 */
public class LoggerFactory {
    //
    //日志等级
    //
    private static volatile Level level = Level.TRACE;
    public static void setLevel(Level level) {
        LoggerFactory.level = level;
    }
    public static Level getLevel() {
        return LoggerFactory.level;
    }

    //
    //书写器
    //
    private static Appender appender = new AppenderSimple();
    public static Appender getAppender() {
        return appender;
    }
    public static void setAppender(Appender appender) {
        LoggerFactory.appender = appender;
    }

    //
    //日志器工厂
    //
    private static ILoggerFactory factory = (name) -> new LoggerSimple(name);
    public static ILoggerFactory getFactory() {
        return factory;
    }
    public static void setFactory(ILoggerFactory factory) {
        LoggerFactory.factory = factory;
    }

    //
    //获取
    //
    public static Logger get(String name) {
        return factory.getLogger(name);
    }
    public static Logger get(Class<?> clz) {
        return factory.getLogger(clz);
    }
}
