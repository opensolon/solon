package org.noear.solon.core.exception;

/**
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
