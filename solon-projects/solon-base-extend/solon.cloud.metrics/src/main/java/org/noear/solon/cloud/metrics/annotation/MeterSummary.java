package org.noear.solon.cloud.metrics.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 用来记录指标的分布
 *
 * @author noear
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterSummary {
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
     * 最大期望值
     * */
    double maxValue() default Double.MAX_VALUE;

    /**
     * 最小期望值
     * */
    double minValue() default 1D;

    /**
     * 百分位
     * */
    double[] percentiles() default {};

    /**
     * 百分位柱状图
     * */
    boolean percentilesHistogram() default false;

    double scale() default 1.0D;

    double[] serviceLevelObjectives() default {};

    /**
     * 标签
     */
    String[] tags() default {};

    /**
     * 描述
     * */
    String description() default "";
}
