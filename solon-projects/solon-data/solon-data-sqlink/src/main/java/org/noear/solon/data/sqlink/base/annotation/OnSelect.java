package org.noear.solon.data.sqlink.base.annotation;

import org.noear.solon.data.sqlink.base.intercept.Interceptor;
import org.noear.solon.data.sqlink.base.intercept.NoInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询时，值进入typeHandler之前和值从typeHandler取出之后
 *
 * @author kiryu1223
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OnSelect {
    /**
     * 参数进入typeHandler之前的时机
     */
    Class<? extends Interceptor<?>> put() default NoInterceptor.class;

    /**
     * 值通过typeHandler取出之后的时机
     */
    Class<? extends Interceptor<?>> get() default NoInterceptor.class;
}
