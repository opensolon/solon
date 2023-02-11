package org.noear.solon.aspect.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 数据访问类注解。此注解会使用asm代理机制
 *
 * @author noear
 * @since 1.1
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dao {
    @Alias("name")
    String value() default ""; //as bean.name

    @Alias("value")
    String name() default "";

    @Note("同时注册类型，仅当名称非空时有效")
    boolean typed() default false;
}
