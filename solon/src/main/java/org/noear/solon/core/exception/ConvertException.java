package org.noear.solon.core.exception;

/**
 * 转换异常
 *
 * @author noear
 * @since 2.4
 */
public class ConvertException extends RuntimeException {
    public ConvertException(Throwable cause) {
        super(cause);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
