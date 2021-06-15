package org.noear.solon.validation.annotation;


import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 不能小于min
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotZero {
    /**
     * param names
     * */
    @Note("param names")
    String[] value() default {};

    String message() default "";
}
