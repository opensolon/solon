package org.noear.solon.validation.annotation;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    long value();

    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
