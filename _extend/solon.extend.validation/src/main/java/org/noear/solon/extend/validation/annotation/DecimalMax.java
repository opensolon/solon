package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMax{
    double value();

    String message() default "";
}
