package org.noear.solon.extend.mybatis;

import java.lang.annotation.*;

/**
 * 例：
 *
 * @Df("db1f") SqlSessionFactory factory;
 * @Df("db1f") SqlSession session;
 * @Df("db1f") Mapper mapper;
 * */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Df {
    /**
     * sqlSessionFactory bean name
     * */
    String value();
}
