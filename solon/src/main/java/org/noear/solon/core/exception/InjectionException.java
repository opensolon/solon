package org.noear.solon.core.exception;

/**
 * 注入异常
 *
 * @author noear
 * @since 1.10
 */
public class InjectionException extends RuntimeException {
    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
