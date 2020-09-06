package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Whitelist {
    String tag() default "";
}
