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
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudBreaker {
    /**
     * 名称，支持配置模式：${xxx}
     * */
    @Note("name")
    String value();
}
