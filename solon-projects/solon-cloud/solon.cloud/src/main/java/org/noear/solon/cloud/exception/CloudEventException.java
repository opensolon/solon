package org.noear.solon.cloud.exception;

import org.noear.solon.exception.SolonException;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventException extends SolonException {
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
