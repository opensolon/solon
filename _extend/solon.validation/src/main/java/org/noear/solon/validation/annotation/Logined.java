package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.3
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logined {
    @Note("logined by session user key")
    String value() default "";

    String message() default "";
}
