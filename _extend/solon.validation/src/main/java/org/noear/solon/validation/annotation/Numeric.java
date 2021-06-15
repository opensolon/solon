package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是个数值
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Numeric {
    /**
     * param names
     * */
    @Note("param names")
    String[] value() default {};

    String message() default "";
}
