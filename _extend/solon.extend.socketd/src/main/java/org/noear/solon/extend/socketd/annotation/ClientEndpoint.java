package org.noear.solon.extend.socketd.annotation;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageWrapper;

import java.lang.annotation.*;
import java.util.function.Supplier;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientEndpoint {
    /**
     * 资源描述
     */
    String value() default "";

    /**
     * 方法
     */
    MethodType method() default MethodType.ALL;

    /**
     * 主机
     */
    String host();

    /**
     * 端口
     */
    int port();

    /**
     * 握手包头
     * */
    String handshakeHeader() default "";

    /**
     * 心跳频率（单位：秒）
     */
    int heartbeatRate() default 30;
}