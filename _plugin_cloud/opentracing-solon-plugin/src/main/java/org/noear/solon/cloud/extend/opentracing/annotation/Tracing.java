package org.noear.solon.cloud.extend.opentracing.annotation;

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
    String[] tags() default {};
}
