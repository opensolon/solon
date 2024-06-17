package org.noear.solon.cloud.exception;

/**
 * 配置异常
 *
 * @author noear
 * @since 1.10
 */
public class CloudConfigException extends CloudException {
    public CloudConfigException(String message){
        super(message, 500);
    }

    public CloudConfigException(Throwable cause){
        super(cause, 500);
    }
}
