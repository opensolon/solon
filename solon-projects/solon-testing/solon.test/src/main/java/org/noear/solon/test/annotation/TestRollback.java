package org.noear.solon.test.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试回滚
 *
 * @author noear
 * @since 1.10
 * @deprecated 2.5
 */
@Note("由 @Rollback 替代")
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestRollback {
    /**
     * 是否回滚
     */
    boolean value() default true;
}
