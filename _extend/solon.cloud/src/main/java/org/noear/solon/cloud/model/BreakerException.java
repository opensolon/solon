package org.noear.solon.cloud.model;

/**
 * 断路器异常
 *
 * @author noear
 * @since 1.3
 */
public class BreakerException extends Exception {
    public BreakerException() {
        super();
    }

    public BreakerException(Throwable cause) {
        super(cause);
    }
}
