package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 配置订阅
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudConfig {
    @Note("key")
    String value();

    /**
     * 分组 （对某些框架来讲，可能没用处）
     * */
    String group() default "";
}
