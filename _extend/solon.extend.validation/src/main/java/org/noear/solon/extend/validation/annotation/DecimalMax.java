package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0.27
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMax{
    double value();

    String message() default "";
}
