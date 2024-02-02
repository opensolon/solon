package org.noear.solon.scheduling.annotation;

import java.lang.annotation.*;

/**
 * 启用异步调度注解
 *
 * @author noear
 * @since 1.12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAsync {
}
