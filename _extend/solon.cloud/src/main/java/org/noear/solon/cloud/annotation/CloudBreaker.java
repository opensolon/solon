package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Note;
import org.noear.solon.cloud.impl.CloudBreakerInterceptor;

import java.lang.annotation.*;

/**
 * 云端断路器申明
 *
 * @author noear
 * @since 1.3
 */
@Around(CloudBreakerInterceptor.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudBreaker {
    @Note("name")
    String value();
}
