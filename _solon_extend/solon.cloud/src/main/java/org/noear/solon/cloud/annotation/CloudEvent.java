package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Alias;
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
     * 主题，支持${xxx}配置
     * */
    @Alias("topic")
    String value() default "";

    /**
     * 主题，支持${xxx}配置
     * */
    @Alias("value")
    String topic() default "";

    /**
     * 订阅级别
     * */
    EventLevel level() default EventLevel.cluster;

    /**
     * 分组，支持${xxx}配置
     * */
    String group() default "";

    /**
     * 通道：用于同时支持多个消息框架
     * */
    String channel() default "";
}
