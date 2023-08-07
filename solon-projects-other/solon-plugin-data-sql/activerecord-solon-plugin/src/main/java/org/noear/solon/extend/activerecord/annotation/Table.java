package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表注解
 *
 * @author noear
 * @since 1.4
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 表名
     * */
    String name();
    /**
     * 主键
     * */
    String primaryKey() default "id";
}

