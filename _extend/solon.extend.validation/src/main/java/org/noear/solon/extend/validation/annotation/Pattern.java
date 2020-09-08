package org.noear.solon.extend.validation.annotation;


import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    String expr();

    String message() default "";
}
