package org.noear.solon.extend.socketd.annotation;

import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientListenEndpoint {
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
     * */
    String host();

    /**
     * 端口
     * */
    int port();

    /**
     * 心跳频率（单位：秒）
     * */
    int heartbeatRate() default 30;
}