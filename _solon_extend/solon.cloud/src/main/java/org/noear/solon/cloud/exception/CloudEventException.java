package org.noear.solon.cloud.exception;

/**
 * @author noear 2021/4/24 created
 */
public class CloudEventException extends RuntimeException{
    public CloudEventException(Throwable cause){
        super(cause);
    }
}
