package org.noear.solon.extend.aspect.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Component;

import java.lang.annotation.*;

/**
 * 此注解会使用asm代理机制
 *
 * @author noear
 * @since 1.1
 */
@Alias(anno = Component.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dao {
}
