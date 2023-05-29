package org.noear.solon.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    HttpPart[] value() default {HttpPart.params};

    int seconds() default 1;

    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
