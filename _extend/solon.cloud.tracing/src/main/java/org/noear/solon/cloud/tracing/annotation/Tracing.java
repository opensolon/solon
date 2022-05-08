package org.noear.solon.cloud.tracing.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.7
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tracing {
    String value() default "";
    String name() default "";

    @Note("缓存标签，多个以逗号隔开")
    String tags() default "";
}
