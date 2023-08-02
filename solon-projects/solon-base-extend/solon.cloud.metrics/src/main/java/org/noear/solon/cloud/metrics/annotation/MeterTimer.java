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
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterTimer {
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
}
