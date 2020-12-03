package org.noear.solon.extend.socketd.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handshake {
    @Note("握手包头")
    String handshakeHeader() default "";
}
