package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 转换异常
 *
 * @author noear
 * @since 2.4
 */
public class ConvertException extends SolonException {
    public ConvertException(Throwable cause) {
        super(cause);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
