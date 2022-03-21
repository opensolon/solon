package org.noear.solon.logging;

import org.noear.solon.Solon;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.model.LoggerLevelEntity;

import java.util.*;

/**
 * 日志选项
 *
 * @author noear
 * @since 1.3
 */
public class LogOptions {

    private static volatile Map<String, LoggerLevelEntity> loggerLevelMap = new LinkedHashMap<>();
    private static volatile boolean loggerLevelMapInited = false;

    /**
     * 添加记录器等级设定
     *
     * @param loggerExpr 记录器表达式
     * @param level      等级
     */
    public static void addLoggerLevel(String loggerExpr, Level level) {
        if (loggerExpr.endsWith(".*")) {
            loggerExpr = loggerExpr.substring(0, loggerExpr.length() - 1);
        }

        if (loggerLevelMap.containsKey(loggerExpr) == false) {
            loggerLevelMap.put(loggerExpr, new LoggerLevelEntity(loggerExpr, level));
        }
    }

    /**
     * 获取所有配置的记录器等级设定
     * */
    public static Collection<LoggerLevelEntity> getLoggerLevels() {
        if (loggerLevelMapInited == false) {
            loggerLevelMapInit();
        }

        return loggerLevelMap.values();
    }


    /**
     * 初始化记录器等级配置
     * */
    public static void getLoggerLevelInit() {
        if (loggerLevelMapInited == false) {
            loggerLevelMapInit();
        }
    }
    /**
     * 获取记录器等级设定
     */
    public static Level getLoggerLevel(String logger) {
        if (loggerLevelMapInited == false) {
            loggerLevelMapInit();
        }

        if (logger == null) {
            return Level.ALL;
        }

        for (LoggerLevelEntity l : loggerLevelMap.values()) {
            if (logger.startsWith(l.getLoggerExpr())) {
                return l.getLevel();
            }
        }

        return Level.ALL;
    }

    /**
     * 初始化记录器默认等级
     */
    private static synchronized void loggerLevelMapInit() {
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
