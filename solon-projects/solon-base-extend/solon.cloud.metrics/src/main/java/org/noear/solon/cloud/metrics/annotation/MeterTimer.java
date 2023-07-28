package org.noear.solon.cloud.metrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 计时器
 *
 * @author bai
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterTimer {

    /**
     * 值
     *
     * @return {@link String}
     */
    String value() default "";

    /**
     * 类型
     *
     * @return {@link String}
     */
    String type() default "timer";

    /**
     * 标签
     *
     * @return {@link String[]}
     */
    String[] tags() default {};
}
