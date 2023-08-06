package org.noear.solon.docs.exception;

/**
 * @author noear
 * @since 2.3
 */
public class DocException extends RuntimeException {
    public DocException(String message) {
        super(message);
    }

    public DocException(String message, Throwable cause) {
        super(message, cause);
    }
}
