package org.noear.solon.cloud.exception;

/**
 * 任务异常
 *
 * @author noear
 * @since 1.10
 */
public class CloudJobException extends CloudException {
    public CloudJobException(Throwable cause) {
        super(cause, 500);
    }

    public CloudJobException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
