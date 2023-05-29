package org.noear.solon.cloud.exception;

/**
 * @author noear
 * @since 1.10
 */
public class CloudJobException extends RuntimeException {
    public CloudJobException(Throwable cause) {
        super(cause);
    }

    public CloudJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
