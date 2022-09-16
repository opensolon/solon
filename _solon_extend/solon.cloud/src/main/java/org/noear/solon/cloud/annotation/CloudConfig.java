package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 云端配置订阅或注入
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudConfig {
    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("name")
    String value() default "";

    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("value")
    String name() default "";

    /**
     * 分组 （对某些框架来讲，可能没用处），支持${xxx}配置
     * */
    String group() default "";

    /**
     * 自动刷新
     * */
    @Note("单例才有自动刷新的必要")
    boolean autoRefreshed() default false;
}
