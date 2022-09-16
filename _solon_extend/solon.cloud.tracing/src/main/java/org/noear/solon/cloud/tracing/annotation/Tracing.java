package org.noear.solon.cloud.tracing.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 跟踪埋点（支持可继承，适合给非控制器用）
 *
 * @author noear
 * @since 1.7
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tracing {
    /**
     * 操作名
     * */
    @Alias("name")
    String value() default "";
    /**
     * 操作名
     * */
    @Alias("value")
    String name() default "";

    @Note("标签，多个以逗号隔开")
    String tags() default "";
}
