package org.noear.solon.extend.aspect.annotation;

import java.lang.annotation.*;

/**
 * 此注解会使用asm代理机制
 *
 * @author noear
 * @since 1.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
}
