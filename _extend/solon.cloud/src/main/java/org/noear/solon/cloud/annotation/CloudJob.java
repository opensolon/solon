package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudJob {
    /**
     * 名称, 支持配置模式：${xxx}
     * */
    @Note("name")
    String value();
    /**
     * 描述
     * */
    String description() default "";
}
