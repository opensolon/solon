package org.noear.solon.cloud.exception;

/**
 * @author noear
 * @since 1.3
 */
public class CloudFileException extends RuntimeException {
    /**
     * @since 1.11
     * */
    public CloudFileException(String message) {
        super(message);
    }

    public CloudFileException(Throwable cause) {
        super(cause);
    }
}
