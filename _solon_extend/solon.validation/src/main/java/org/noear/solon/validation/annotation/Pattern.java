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
     * 提示消息
     * */
    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
