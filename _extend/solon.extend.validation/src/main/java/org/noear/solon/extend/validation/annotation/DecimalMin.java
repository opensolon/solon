package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalMin {
    double value();

    String message() default "";
}
