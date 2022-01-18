package org.noear.solon.extend.retry.annotation;

import java.lang.annotation.*;

/**
 * 异步执行启用注解
 *
 * @author noear
 * @since 1.6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRetry {
}
