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
    /**
     * 路径
     * */
    @Alias("path")
    String value() default "";

    /**
     * 路径
     * */
    @Alias("value")
    String path() default "";

    /**
     * 架构
     */
    String schema() default "";
}