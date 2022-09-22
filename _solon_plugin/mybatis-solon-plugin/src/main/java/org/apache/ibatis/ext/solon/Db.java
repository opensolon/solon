package org.apache.ibatis.ext.solon;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db("db1") SqlSessionFactory factory;
 * @Db("db1") SqlSession session;
 * @Db("db1") Mapper mapper;
 * @see org.apache.ibatis.solon.annotation.Db
 * @deprecated 1.8
 * */
@Note("switch to: org.apache.ibatis.solon.annotation.Db")
@Deprecated
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {
    /**
     * sqlSessionFactory bean name
     * */
    String value() default "";
}
