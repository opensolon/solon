package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudJob {
    @Note("name")
    String value() default "";
}
