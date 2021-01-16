package org.noear.solon.cloud.annotation;

import java.lang.annotation.*;

/**
 * 消息订阅
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudEvent {
    String topic();

    /**
     * 队列
     * */
    String queue() default "";
}
