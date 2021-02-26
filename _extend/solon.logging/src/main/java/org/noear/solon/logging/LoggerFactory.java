package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;

/**
 * @author noear 2021/2/26 created
 */
public class LoggerFactory {
    //
    //日志等级
    //
    private static volatile Level level = Level.WARN;
    public static void setLevel(Level level) {
        LoggerFactory.level = level;
    }
    public static Level getLevel() {
        return LoggerFactory.level;
    }
}
