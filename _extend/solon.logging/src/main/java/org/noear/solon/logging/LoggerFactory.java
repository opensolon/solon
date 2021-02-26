package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;
import org.slf4j.Logger;

/**
 * @author noear
 * @since 1.3
 */
public class LoggerFactory {
    //
    //默认日志等级
    //
    private static volatile Level level = Level.WARN;

    public static void setLevel(Level level) {
        LoggerFactory.level = level;
    }

    public static Level getLevel() {
        return LoggerFactory.level;
    }

    //
    // 获取记录器
    //
    public static Logger getLogger(String loggerName) {
        return org.slf4j.LoggerFactory.getLogger(loggerName);
    }

    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
}
