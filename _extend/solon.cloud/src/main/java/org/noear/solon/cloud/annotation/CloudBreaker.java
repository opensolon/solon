package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 云端断路器申明
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudBreaker {
    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("name")
    String value() default "";
    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("value")
    String name() default "";
}
