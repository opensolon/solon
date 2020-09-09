package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0.23
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    long value();

    String message() default "";
}
