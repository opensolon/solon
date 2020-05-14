package org.noear.solonclient.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XClient {
    String value() default "";
    String[] headers() default {};
}

