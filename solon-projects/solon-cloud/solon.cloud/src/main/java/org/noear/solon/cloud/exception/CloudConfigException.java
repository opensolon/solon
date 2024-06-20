package org.noear.solon.cloud.exception;

/**
 * Cloud 配置异常
 *
 * @author noear
 * @since 1.10
 */
public class CloudConfigException extends CloudException {
    public CloudConfigException(String message){
        super(message);
    }

    public CloudConfigException(Throwable cause){
        super(cause);
    }
}
