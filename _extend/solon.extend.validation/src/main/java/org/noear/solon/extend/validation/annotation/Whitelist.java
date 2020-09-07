package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XNote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Whitelist {
    @XNote("whitelist tag")
    String value() default "";

    String message() default "";
}
