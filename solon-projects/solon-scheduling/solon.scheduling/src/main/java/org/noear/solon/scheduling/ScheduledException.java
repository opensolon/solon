package org.noear.solon.scheduling;

import org.noear.solon.exception.SolonException;

/**
 * 计划任务异常
 *
 * @author noear
 * @since 1.5
 */
public class ScheduledException extends SolonException {
    public ScheduledException(Throwable cause) {
        super(cause);
    }

    public ScheduledException(String message, Throwable cause) {
        super(message, cause);
    }
}
