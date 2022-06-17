package org.noear.solon.async.annotation;

import java.lang.annotation.*;

/**
 * 异步执行注解
 *
 * @author noear
 * @since 1.6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
}
