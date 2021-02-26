package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;

/**
 * @author noear
 * @since 1.3
 */
public class LoggerOptions {
    //
    //默认日志等级
    //
    private static volatile Level level = Level.WARN;

    public static void setLevel(Level level) {
        LoggerOptions.level = level;
    }

    public static Level getLevel() {
        return LoggerOptions.level;
    }

}
