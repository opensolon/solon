package org.noear.solon.logging;

import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.model.LoggerLevelEntity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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


    private static volatile Set<String> loggerCached = new LinkedHashSet<>();
    private static volatile List<LoggerLevelEntity> loggerLevelEntities = new ArrayList<>();

    public static void addLoggerLevel(String loggerExpr, Level level) {
        if (loggerExpr.endsWith(".*")) {
            loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
        }

        if (loggerCached.contains(loggerExpr) == false) {
            loggerCached.add(loggerExpr);
            loggerLevelEntities.add(new LoggerLevelEntity(loggerExpr, level));
        }
    }

    public static Level getLoggerLevel(String logger) {
        for (LoggerLevelEntity l : loggerLevelEntities) {
            if (logger.startsWith(l.getLoggerExpr())) {
                return l.getLevel();
            }
        }

        return getLevel();
    }
}
