package org.noear.solon.logging;

import org.noear.solon.SolonApp;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.model.LoggerLevelEntity;

import java.util.*;

/**
 * @author noear
 * @since 1.3
 */
public class LogOptions {
    //
    //默认日志等级
    //
    private static volatile Level level = Level.INFO;

    public static void setLevel(Level level) {
        LogOptions.level = level;
    }

    public static Level getLevel() {
        return LogOptions.level;
    }


    private static volatile Map<String, LoggerLevelEntity> loggerLevelMap = new LinkedHashMap<>();

    public static void addLoggerLevel(String loggerExpr, Level level) {
        if (loggerExpr.endsWith(".*")) {
            loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
        }

        if (loggerLevelMap.containsKey(loggerExpr) == false) {
            loggerLevelMap.put(loggerExpr, new LoggerLevelEntity(loggerExpr, level));
        }
    }

    public static Level getLoggerLevel(String logger) {
        for (LoggerLevelEntity l : loggerLevelMap.values()) {
            if (logger.startsWith(l.getLoggerExpr())) {
                return l.getLevel();
            }
        }

        return getLevel();
    }
}
