package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sql注解
 * <p>用于指定模板中的SQL名。如果使用{}包裹，则表示为直接执行的SQL语句，如：</p>
 * <pre>
 * @Sql(" { select count(*) from user where status=#para(status) } ")
 * public long getCount();
 * </pre>
 * <p>也可直接指定绝对SQL（命名空间.SQL名）。如：</p>
 * <pre>
 * @Sql("user.getCount")
 * public long getCount();
 * </pre>
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Sql {

    /**
     * 是否更新SQL
     */
    boolean isUpdate() default false;

    /**
     * value of sql.
     */
    String value();
}
