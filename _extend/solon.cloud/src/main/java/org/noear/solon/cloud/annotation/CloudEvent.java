package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 云端事件订阅
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudEvent {
    /**
     * 名称，支持${xxx}配置
     * */
    @Note("name")
    String value() default "";

    /**
     * 名称，支持${xxx}配置
     * */
    String name() default "";

    /**
     * 分组，支持${xxx}配置
     * */
    String group() default "";

    /**
     * 订阅级别
     * */
    EventLevel level() default EventLevel.cluster;

    /**
     * 通道：用于同时支持多个消息框架
     * */
    String channel() default "";
}
