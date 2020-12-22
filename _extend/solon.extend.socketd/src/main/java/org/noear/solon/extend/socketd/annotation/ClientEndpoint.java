package org.noear.solon.extend.socketd.annotation;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientEndpoint {
    /**
     * 资源描述
     */
    String uri();

    /**
     * 方法
     */
    MethodType method() default MethodType.ALL;

    /**
     * 握手包头
     * */
    @Note("握手包头")
    String handshakeHeader() default "";

    /**
     * 心跳频率（单位：秒）
     */
    @Note("心跳频率（单位：秒）")
    int heartbeatRate() default 30;
}