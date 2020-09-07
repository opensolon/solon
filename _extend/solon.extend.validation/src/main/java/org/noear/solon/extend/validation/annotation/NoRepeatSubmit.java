package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    HttpPart[] value() default {HttpPart.params};

    int seconds() default 1;

    String message() default "";
}
