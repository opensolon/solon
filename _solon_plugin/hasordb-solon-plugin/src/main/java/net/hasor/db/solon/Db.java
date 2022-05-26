package net.hasor.db.solon;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 *
 * 例：
 * @Db("db1")
 *
 * @author noear
 * @since 1.6
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
