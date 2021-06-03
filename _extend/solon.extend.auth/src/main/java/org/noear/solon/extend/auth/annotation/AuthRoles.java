package org.noear.solon.extend.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要特定角色（需要用户按需使用）
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRoles {
    /**
     * 角色
     * */
    String[] value();
    /**
     * 逻辑关系
     * */
    Logical logical() default Logical.OR;
}