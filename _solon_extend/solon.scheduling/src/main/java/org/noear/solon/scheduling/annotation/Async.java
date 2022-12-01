package org.noear.solon.scheduling.annotation;

import org.noear.solon.annotation.Around;
import org.noear.solon.scheduling.async.AsyncInterceptor;

import java.lang.annotation.*;

/**
 * 异步执行注解
 *
 * @author noear
 * @since 1.6
 */
@Around(value = AsyncInterceptor.class, index = -99)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
}
