package org.noear.solon.extend.aopx.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    String value() default "";
}
