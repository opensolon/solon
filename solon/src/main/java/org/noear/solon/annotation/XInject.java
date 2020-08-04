package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入到字段或参数（参数只在XConfiguration有效）
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInject {
    String value() default "";
}
