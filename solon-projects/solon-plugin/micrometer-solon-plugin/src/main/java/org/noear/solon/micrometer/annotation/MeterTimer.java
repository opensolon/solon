package org.noear.solon.micrometer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 计计时器
 * 统计接口耗时
 *
 * @author bai
 * @date 2023/07/26
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
