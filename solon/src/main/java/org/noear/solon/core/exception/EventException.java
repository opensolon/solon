package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 事件异常
 *
 * @author noear
 * @since 1.11
 */
public class EventException extends SolonException {
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}
