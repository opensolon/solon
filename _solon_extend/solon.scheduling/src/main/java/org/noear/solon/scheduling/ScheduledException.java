package org.noear.solon.scheduling;

/**
 * 计划任务异常
 *
 * @author noear
 * @since 1.5
 */
public class ScheduledException extends RuntimeException {
    public ScheduledException(Throwable cause) {
        super(cause);
    }

    public ScheduledException(String message, Throwable cause) {
        super(message, cause);
    }
}
