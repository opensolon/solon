package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.4
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 表达
     * */
    String name();
    /**
     * 主键
     * */
    String primaryKey() default "id";
}

