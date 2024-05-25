package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 状态异常（用传递处理状态，如 4xx）
 *
 * @author noear
 * @since 2.4
 */
public class StatusException extends SolonException {
    private int statusPreivew;

    /**
     * 获取状态预览
     * */
    public int getStatusPreivew() {
        return statusPreivew;
    }

    public StatusException(Throwable cause, int statusPreivew) {
        super(cause);
        this.statusPreivew = statusPreivew;
    }

    public StatusException(String message, Throwable cause, int statusPreivew) {
        super(message, cause);
        this.statusPreivew = statusPreivew;
    }

    public StatusException(String message, int statusPreivew) {
        super(message);
        this.statusPreivew = statusPreivew;
    }
}