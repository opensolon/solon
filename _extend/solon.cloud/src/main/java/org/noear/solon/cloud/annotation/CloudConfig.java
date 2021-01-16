package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 配置订阅
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudConfig {
    @Note("group/key")
    String value();
}
