package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注入
 *
 * 可注入配置和BEAN
 * */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInject {
    String value() default "";
}
