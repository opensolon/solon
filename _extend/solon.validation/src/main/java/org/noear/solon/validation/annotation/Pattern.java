package org.noear.solon.validation.annotation;


import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    @Note("正则表达式")
    String value();

    String message() default "";
}
