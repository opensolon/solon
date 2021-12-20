package org.noear.solon.annotation;

import org.noear.solon.core.Constants;

import java.lang.annotation.*;

/**
 * 参数（主要修饰参数，很少用到）
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    /**
     * 参数名
     * */
    @Alias("name")
    String value() default "";
    /**
     * 参数名
     * */
    @Alias("value")
    String name() default "";
    /**
     * 必须的(只做标识，不做检查)
     * */
    boolean required() default false;

    /**
     * 默认值
     * */
    String defaultValue() default Constants.PARM_UNDEFINED_VALUE;
}
