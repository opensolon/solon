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
     * 主题，支持配置模式：${xxx}
     * */
    @Note("topic")
    String value();

    /**
     * 队列，支持配置模式：${xxx}
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
