package org.noear.solon.data.sqlink.annotation;

import org.noear.solon.data.sqlink.base.intercept.Interceptor;
import org.noear.solon.data.sqlink.base.intercept.NoInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库取出值时拦截器
 *
 * @author kiryu1223
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OnGet {
    /**
     * SQL => typeHandler => interceptor => value
     */
    Class<? extends Interceptor<?>> value();
}
