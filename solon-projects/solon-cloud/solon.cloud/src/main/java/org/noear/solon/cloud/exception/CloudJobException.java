package org.noear.solon.cloud.exception;

import org.noear.solon.exception.SolonException;

/**
 * @author noear
 * @since 1.10
 */
public class CloudJobException extends SolonException {
    public CloudJobException(Throwable cause) {
        super(cause);
    }

    public CloudJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
