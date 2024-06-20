package org.noear.solon.cloud.exception;

import org.noear.solon.core.exception.StatusException;

/**
 * Cloud 状态异常（主要用于触发 4xx 状态异常）
 *
 * @author noear
 * @since 2.8
 */
public class CloudStatusException extends StatusException {
    public CloudStatusException(Throwable cause, int code) {
        super(cause, code);
    }

    public CloudStatusException(String message, int code) {
        super(message, code);
    }

    public CloudStatusException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }
}