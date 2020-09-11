package org.noear.solon.extend.beetlsql;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db("db1") SQLManager sqlManager;
 * @Db("db1") Mapper mapper;
 *
 * @author noear
 * */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {
    /**
     * datsSource bean name
     * */
    String value() default "";
}
