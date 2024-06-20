package org.noear.solon.cloud.exception;

/**
 * Cloud 文件异常
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileException extends CloudException {
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
