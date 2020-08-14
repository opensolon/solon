package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 拦截器（争对全局路径拦截）
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInterceptor {
}

