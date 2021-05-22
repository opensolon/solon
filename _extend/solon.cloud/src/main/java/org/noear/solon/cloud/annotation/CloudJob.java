package org.noear.solon.cloud.annotation;

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
     * 名称
     * */
    String name();
    /**
     * 描述
     * */
    String description() default "";
}
