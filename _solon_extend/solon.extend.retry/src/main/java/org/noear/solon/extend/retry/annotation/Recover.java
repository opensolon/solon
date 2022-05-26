package org.noear.solon.extend.retry.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Recover {
    Class<? extends Throwable>[] value();
    int maxAttempts() default 0;
}
