package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XNote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author noear
 * @since 1.0.28
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    @XNote("正则表达式")
    String value() default  "";

    String message() default "";
}
