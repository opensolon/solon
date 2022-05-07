package org.noear.solon.cloud.extend.opentracing.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tracing {
    String value() default "";
    String name() default "";

    @Note("缓存标签，多个以逗号隔开")
    String tags() default "";
}
