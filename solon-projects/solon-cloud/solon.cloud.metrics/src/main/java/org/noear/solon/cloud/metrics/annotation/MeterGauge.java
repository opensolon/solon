package org.noear.solon.cloud.metrics.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 用来记录单个的变化的值（例如：温度，气压）
 *
 * @author noear
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterGauge {
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
     * 描述
     *
     * @return {@link String}
     */
    String description() default "";
}
