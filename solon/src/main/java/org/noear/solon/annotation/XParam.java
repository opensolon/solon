package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用
 * */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XParam {
    String value() default "";
}
