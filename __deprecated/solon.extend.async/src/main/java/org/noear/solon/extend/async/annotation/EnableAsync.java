package org.noear.solon.extend.async.annotation;

import java.lang.annotation.*;

/**
 * 异步执行启用注解
 *
 * @author noear
 * @since 1.6
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAsync {
}
