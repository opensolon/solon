package org.noear.solon.extend.mybatis;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Df("db1f") SqlSessionFactory factory;
 * @Df("db1f") MybatisProxy proxy;(用于发起事务)
 * @Df("db1f") SqlSession session;
 * @Df("db1f") Mapper mapper;
 * */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Df {
    /**
     * sqlSessionFactory bean name
     * */
    String value() default "";
}
