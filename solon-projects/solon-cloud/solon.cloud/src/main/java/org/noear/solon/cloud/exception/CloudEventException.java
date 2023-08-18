package org.noear.solon.cloud.exception;

/**
 * @author noear 2021/4/24 created
 */
public class CloudEventException extends RuntimeException {
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
