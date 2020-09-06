package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    HttpPart[] value() default {HttpPart.params};

    int seconds() default 3;

    String message() default "";
}
