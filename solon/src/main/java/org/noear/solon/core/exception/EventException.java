package org.noear.solon.core.exception;

/**
 * 事件异常
 *
 * @author noear
 * @since 1.11
 */
public class EventException extends RuntimeException {
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}
