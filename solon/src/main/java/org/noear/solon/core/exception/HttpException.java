package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * Http 异常
 *
 * @author noear
 * @since 2.4
 */
public class HttpException extends SolonException {
    private int statusPreivew;

    public int getStatusPreivew() {
        return statusPreivew;
    }

    public HttpException(Throwable cause, int statusPreivew) {
        super(cause);
        this.statusPreivew = statusPreivew;
    }

    public HttpException(String message, Throwable cause, int statusPreivew) {
        super(message, cause);
        this.statusPreivew = statusPreivew;
    }
}