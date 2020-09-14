package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0.28
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    long value();

    String message() default "";
}
