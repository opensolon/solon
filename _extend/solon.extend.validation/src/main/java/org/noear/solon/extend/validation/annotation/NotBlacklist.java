package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 黑名单注解
 *
 * @author noear
 * @since 1.3
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlacklist {
    @Note("blacklist name")
    String value() default "";

    String message() default "";
}
