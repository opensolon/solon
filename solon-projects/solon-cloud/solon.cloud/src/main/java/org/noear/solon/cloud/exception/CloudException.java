package org.noear.solon.cloud.exception;

import org.noear.solon.exception.SolonException;

/**
 * @author noear
 * @since 2.8
 */
public class CloudException extends SolonException {
    public CloudException(Throwable cause) {
        super(cause);
    }

    public CloudException(String message) {
        super(message);
    }

    public CloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
