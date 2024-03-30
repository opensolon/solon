package org.noear.solon.exception;

/**
 * Solon 异常
 *
 * @author noear
 * @since 2.7
 */
public class SolonException extends RuntimeException {
    public SolonException(Throwable cause) {
        super(cause);
    }

    public SolonException(String message) {
        super(message);
    }

    public SolonException(String message, Throwable cause) {
        super(message, cause);
    }
}
