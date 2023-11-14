package org.noear.solon.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试回滚
 *
 * @author noear
 * @since 1.10
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rollback {
    /**
     * 是否回滚
     */
    boolean value() default true;
}
