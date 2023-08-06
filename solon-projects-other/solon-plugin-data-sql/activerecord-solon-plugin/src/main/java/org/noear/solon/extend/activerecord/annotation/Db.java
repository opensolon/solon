package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 数据库注解
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Db {
    /**
     * 数据源Bean实例名称
     */
    String value() default "";
}
