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
     * server host
     * */
    String host();

    /**
     * server port
     * */
    int port();
}