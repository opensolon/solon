package org.apache.ibatis.solon.annotation;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db("db1") SqlSessionFactory factory;
 * @Db("db1") SqlSession session;
 * @Db("db1") Mapper mapper;
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
