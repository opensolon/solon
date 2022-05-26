package org.noear.solon.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 认证特定权限（需要用户按需使用）
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPermissions {
    /**
     * 权限
     * */
    String[] value();
    /**
     * 认证的逻辑关系
     * */
    Logical logical() default Logical.OR;
}
