package org.hibernate.solon.annotation;


import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {
    /**
     * ds bean name
     * */
    String value() default "";
}