package org.noear.solon.cloud.exception;

/**
 * Cloud 融断异常
 *
 * @author noear
 * @since 2.8
 */
public class CloudBreakerException extends CloudStatusException {
    public CloudBreakerException(String message) {
        super(message, 429);
    }
}
