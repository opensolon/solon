package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 参数（主要修饰参数）
 * */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XParam {
    String value() default "";
}
