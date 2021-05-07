package org.noear.solon.extend.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要权限
 *
 * @author noear
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPermissions {
    String[] value();
    Logical logical() default Logical.AND;
    String message() default "";
}
