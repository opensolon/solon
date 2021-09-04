package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    @Note("正则表达式")
    String value();

    /**
     * 或者可以为空
     * */
    boolean orEmpty() default false;

    /**
     * 提示消息
     * */
    String message() default "";
}
