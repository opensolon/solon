package org.noear.solon.net.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerEndpoint {
    @Alias("path")
    String value() default "";

    @Alias("value")
    String path() default "";
}