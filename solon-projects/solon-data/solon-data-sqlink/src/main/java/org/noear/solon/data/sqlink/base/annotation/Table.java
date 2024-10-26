package org.noear.solon.data.sqlink.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table
{
    /**
     * 所属
     */
    String schema() default "";
    /**
     * 数据库表名
     */
    String value() default "";
}
