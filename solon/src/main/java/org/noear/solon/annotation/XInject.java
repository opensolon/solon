package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 * */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInject {
    String value() default "";
}
