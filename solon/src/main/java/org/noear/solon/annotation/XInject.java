package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入到字段或参数（参数只在XConfiguration有效）
 *
 * 禁止注入在类型上；可避免让非单例bean的注入变复杂，进而避免影有响性能
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInject {
    String value() default "";
}
