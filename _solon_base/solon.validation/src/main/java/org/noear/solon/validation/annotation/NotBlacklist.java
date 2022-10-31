package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 黑名单注解（可继承）
 *
 * @author noear
 * @since 1.3
 * */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlacklist {
    @Note("blacklist name")
    String value() default "";

    String message() default "";

    /**
     * 校验分组
     * */
    Class<?>[] groups() default {};
}
