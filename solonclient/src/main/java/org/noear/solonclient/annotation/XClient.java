package org.noear.solonclient.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XClient {
    String value() default "";
    String[] headers() default {};
}

