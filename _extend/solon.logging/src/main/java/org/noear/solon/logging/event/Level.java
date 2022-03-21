package org.noear.solon.logging.event;

/**
 * 日志等级
 *
 * @author noear
 * @since 1.0
 */
public enum Level {
    /**
     * 相当于未配置
     */
    ALL(0),
    TRACE(10),
    DEBUG(20),
    INFO(30),
    WARN(40),
    ERROR(50);

    public final int code;

    public static Level of(int code, Level def) {
        for (Level v : values()) {
            if (v.code == code) {
                return v;
            }
        }

        return def;
    }

    public static Level of(String name, Level def) {
        if (name == null || name.length() == 0) {
            return def;
        }

        switch (name.toUpperCase()) {
            case "TRACE":
                return TRACE;
            case "DEBUG":
                return DEBUG;
            case "INFO":
                return INFO;
            case "WARN":
                return WARN;
            case "ERROR":
                return ERROR;
            default:
                return def;
        }
    }

    Level(int code) {
        this.code = code;
    }
}
