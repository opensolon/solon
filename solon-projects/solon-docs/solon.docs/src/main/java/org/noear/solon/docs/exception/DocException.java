package org.noear.solon.docs.exception;

import org.noear.solon.exception.SolonException;

/**
 * 文档异常
 *
 * @author noear
 * @since 2.3
 */
public class DocException extends SolonException {
    public DocException(String message) {
        super(message);
    }

    public DocException(String message, Throwable cause) {
        super(message, cause);
    }
}
