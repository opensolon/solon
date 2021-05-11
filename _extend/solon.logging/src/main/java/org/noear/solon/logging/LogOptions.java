package org.noear.solon.logging;

import org.noear.solon.Solon;
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
    private static volatile Level level = Level.TRACE;

    public static void setLevel(Level level) {
        LogOptions.level = level;
    }

    public static Level getLevel() {
        return LogOptions.level;
    }


    private static volatile Map<String, LoggerLevelEntity> loggerLevelMap = new LinkedHashMap<>();
    private static volatile boolean loggerLevelMapInited = false;

    public static void addLoggerLevel(String loggerExpr, Level level) {
        if (loggerExpr.endsWith(".*")) {
            loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
        }

        if (loggerLevelMap.containsKey(loggerExpr) == false) {
            loggerLevelMap.put(loggerExpr, new LoggerLevelEntity(loggerExpr, level));
        }
    }

    public static Level getLoggerLevel(String logger) {
        if (loggerLevelMapInited == false) {
            loggerLevelMapInit();
        }

        for (LoggerLevelEntity l : loggerLevelMap.values()) {
            if (logger.startsWith(l.getLoggerExpr())) {
                return l.getLevel();
            }
        }

        return getLevel();
    }

    protected static synchronized void loggerLevelMapInit() {
        if (loggerLevelMapInited) {
            return;
        }

        if (Solon.global() == null) {
            return;
        }

        loggerLevelMapInited = true;

        Properties props = Solon.cfg().getProp("solon.logging.logger");

        if (props.size() > 0) {
            props.forEach((k, v) -> {
                String key = (String) k;
                String val = (String) v;

                if (key.endsWith(".level")) {
                    String loggerExpr = key.substring(0, key.length() - 6);

                    LogOptions.addLoggerLevel(loggerExpr, Level.of(val, Level.INFO));
                }
            });
        }
    }
}
