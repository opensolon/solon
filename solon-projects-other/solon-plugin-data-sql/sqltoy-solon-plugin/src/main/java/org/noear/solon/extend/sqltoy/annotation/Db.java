package org.noear.solon.extend.sqltoy.annotation;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db SqlToyLazyDao dao;
 * @Db SqlToyCRUDService crudService;
 * @Db XXMapper mapper;
 * @Db("db1") SqlToyLazyDao dao; //指定数据源
 * */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {
    /**
     * datasource name
     * */
    String value() default "";
}
