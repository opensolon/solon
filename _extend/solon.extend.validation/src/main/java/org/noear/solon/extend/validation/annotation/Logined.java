package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
