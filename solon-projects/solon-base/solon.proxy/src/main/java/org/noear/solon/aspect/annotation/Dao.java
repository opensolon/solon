package org.noear.solon.aspect.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.ProxyComponent;

import java.lang.annotation.*;

/**
 * 数据访问类注解（未来会弃用，建议改用 @ProxyComponent）
 *
 * @see ProxyComponent
 * @deprecated 2.3
 * @author noear
 * @since 1.1
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dao {
    @Alias("name")
    String value() default ""; //as bean.name

    @Alias("value")
    String name() default "";

    /**
     * 同时注册类型，仅当名称非空时有效
     * */
    boolean typed() default false;
}
