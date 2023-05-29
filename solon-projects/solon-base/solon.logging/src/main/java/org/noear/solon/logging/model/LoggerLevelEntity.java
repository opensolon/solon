package org.noear.solon.logging.model;

import org.noear.solon.logging.event.Level;

import java.util.Objects;

/**
 * 记录器级别实例
 *
 * @author noear
 * @since 1.3
 * @see org.noear.solon.logging.LogOptions
 */
public class LoggerLevelEntity {
    private final String loggerExpr;
    private final Level level;

    /**
     * 获取级别
     * */
    public Level getLevel() {
        return level;
    }

    /**
     * 获取记录器表达式
     * */
    public String getLoggerExpr() {
        return loggerExpr;
    }

    public LoggerLevelEntity(String loggerExpr, Level level) {
        this.loggerExpr = loggerExpr;
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggerLevelEntity)) return false;
        LoggerLevelEntity that = (LoggerLevelEntity) o;
        return Objects.equals(loggerExpr, that.loggerExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loggerExpr);
    }
}
