package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 白名单注解（可继承）
 *
 * @author noear
 * @since 1.0
 * */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Whitelist {
    @Note("whitelist name")
    String value() default "";

    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
