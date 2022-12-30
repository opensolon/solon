package org.noear.solon.cloud.exception;

/**
 * @author noear
 * @since 1.3
 */
public class CloudFileException extends RuntimeException {
    /**
     * @since 1.11
     */
    public CloudFileException(String message) {
        super(message);
    }

    /**
     * @since 1.12
     */
    public CloudFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudFileException(Throwable cause) {
        super(cause);
    }
}
