package org.noear.solon.extend.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 消息订阅
 *
 * @author noear 2021/1/14 created
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudEvent {
    @Note("topic")
    String value();
}
