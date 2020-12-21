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
    String name() default "";
    /**
     * 格式（主要为日期之类的服务）
     * */
    String format() default "";
    /**
     * 必须的(只做标识，不做检查)
     * */
    boolean required() default false;

    /**
     * 默认值
     * */
    String defaultValue() default Constants.PARM_UNDEFINED_VALUE;

    /**
     * 使用头
     * */
    String headerName() default "";

    /**
     * 使用特性
     * */
    String attrName() default "";
}
