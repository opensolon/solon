package org.noear.solon.core.exception;

import org.noear.solon.exception.SolonException;

/**
 * 构造异常
 *
 * @author noear
 * @since 2.8
 */
public class ConstructionException extends SolonException {
    public ConstructionException(Throwable cause) {
        super(cause);
    }

    public ConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}