package org.noear.solon.cloud.exception;

import org.noear.solon.core.exception.StatusException;

/**
 * @author noear
 * @since 2.8
 */
public class CloudException extends StatusException {
    public CloudException(Throwable cause, int code) {
        super(cause, code);
    }

    public CloudException(String message, int code) {
        super(message, code);
    }

    public CloudException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }
}
