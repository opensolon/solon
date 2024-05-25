package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 状态异常（用传递处理状态，如 4xx）
 *
 * @author noear
 * @since 2.4
 */
public class StatusException extends SolonException {
    private int code;

    /**
     * 获取状态码
     * */
    public int getCode() {
        return code;
    }

    public StatusException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public StatusException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public StatusException(String message, int code) {
        super(message);
        this.code = code;
    }
}