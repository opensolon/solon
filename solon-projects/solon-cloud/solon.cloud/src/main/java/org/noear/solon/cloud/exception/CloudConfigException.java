package org.noear.solon.cloud.exception;

import org.noear.solon.exception.SolonException;

/**
 * @author noear
 * @since 1.10
 */
public class CloudConfigException extends SolonException {
    public CloudConfigException(String message){
        super(message);
    }

    public CloudConfigException(Throwable cause){
        super(cause);
    }
}
