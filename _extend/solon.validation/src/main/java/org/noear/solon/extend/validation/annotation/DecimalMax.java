package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMax{
    double value();

    String message() default "";
}
