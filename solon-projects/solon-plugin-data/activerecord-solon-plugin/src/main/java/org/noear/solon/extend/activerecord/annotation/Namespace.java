package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命名空间注解
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,  ElementType.TYPE})
public @interface Namespace {
    /**
     * 命名空间
     */
    String value();

}
