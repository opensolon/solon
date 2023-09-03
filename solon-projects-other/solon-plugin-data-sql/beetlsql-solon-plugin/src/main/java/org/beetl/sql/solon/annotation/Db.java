package org.beetl.sql.solon.annotation;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db("db1") SQLManager sqlManager;
 * @Db("db1") Mapper mapper;
 *
 * @author noear
 * @since 2020-09-01
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {
    /**
     * datsSource bean name
     * */
    String value() default "";
}
