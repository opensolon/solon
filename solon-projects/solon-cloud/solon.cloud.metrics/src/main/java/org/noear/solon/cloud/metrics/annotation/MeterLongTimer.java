package org.noear.solon.cloud.metrics.annotation;


import org.noear.solon.annotation.Alias;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来记录事件的持续时间
 *
 * @author bai
 * @date 2.5
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterLongTimer {


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
     *
     * @return {@link String[]}
     */
    String[] tags() default {};


    /**
     * 描述
     *
     * @return {@link String}
     */
    String description() default "";

    /**
     * 百分位数
     *
     * @return {@link double[]}
     */
    double[] percentiles() default {1.0};

}
