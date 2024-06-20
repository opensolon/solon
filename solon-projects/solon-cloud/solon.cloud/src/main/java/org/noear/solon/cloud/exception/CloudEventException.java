package org.noear.solon.cloud.exception;

/**
 * Cloud 事件异常
 *
 * @author noear
 * @since 1.3
 */
public class CloudEventException extends CloudException {
    public CloudEventException(Throwable cause) {
        super(cause);
    }

    /**
     * @since 2.4
     * */
    public CloudEventException(String message) {
        super(message);
    }
}
