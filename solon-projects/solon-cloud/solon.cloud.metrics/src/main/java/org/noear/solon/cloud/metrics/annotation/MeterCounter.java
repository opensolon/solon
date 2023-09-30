package org.noear.solon.cloud.metrics.annotation;


import org.noear.solon.annotation.Alias;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于不断累加值
 *
 * @author bai
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterCounter {
    /**
     * 名称
     */
    @Alias("name")
    String value() default "";

    /**
     * 名称
     */
    @Alias("value")
    String name() default "";

    /**
     * 标签
     */
    String[] tags() default {};

    /**
     * 单位
     */
    String unit() default "";


    /**
     * 描述
     */
    String description() default "";
}
