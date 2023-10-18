package org.hibernate.solon.annotation;


import org.noear.solon.annotation.Alias;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * hibernate注解
 * 增强了Tran注解的功能
 * 1.兼容了hibernate的事务控制
 * 2.增加了多数据源的事务控制
 *
 * @author bai
 * @date 2023/10/18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HibernateTran  {

    /**
     * ds bean name
     * */
    @Alias("name")
    String value() default "";

    /**
     * ds bean name
     * */
    @Alias("value")
    String name() default "";

}
