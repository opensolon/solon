package org.noear.solon.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMin {
    double value();

    String message() default "";
}
