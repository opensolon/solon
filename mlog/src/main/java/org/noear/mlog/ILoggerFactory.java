package org.noear.mlog;

/**
 * 日志工厂
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface ILoggerFactory {
    Logger getLogger(String name);

    default Logger getLogger(Class<?> clz) {
        return new LoggerSimple(clz);
    }
}
