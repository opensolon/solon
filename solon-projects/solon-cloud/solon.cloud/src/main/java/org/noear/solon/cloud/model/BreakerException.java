package org.noear.solon.cloud.model;

import org.noear.solon.exception.SolonException;

/**
 * 断路器异常
 *
 * @author noear
 * @since 1.3
 */
public class BreakerException extends SolonException {
    public BreakerException() {
        super();
    }

    public BreakerException(Throwable cause) {
        super(cause);
    }
}
