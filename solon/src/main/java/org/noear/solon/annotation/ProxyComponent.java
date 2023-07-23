package org.noear.solon.annotation;

import java.lang.annotation.*;


/**
 * 代理托管组件（支持代理机制，即支持拦截机制）
 *
 * @author noear
 * @since 2.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyComponent {
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    /**
     * 标签，用于快速查找
     * */
    String tag() default "";

    /**
     * 同时注册类型，仅当名称非空时有效
     * */
    boolean typed() default false;

    /**
     * 排序（只对部分类型有效）
     * */
    int index() default 0;
}
