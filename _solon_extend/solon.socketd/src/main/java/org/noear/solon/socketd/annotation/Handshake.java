package org.noear.solon.socketd.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handshake {
    /**
     * 握手包头
     * */
    String handshakeHeader() default "";
}
