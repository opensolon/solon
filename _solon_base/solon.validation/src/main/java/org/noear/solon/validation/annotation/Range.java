package org.noear.solon.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author noear
 * @since 1.11
 * */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    long min() default 0L;
    long max() default Long.MAX_VALUE;

    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
