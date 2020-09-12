package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用 组件
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XBean {
    @XNote("注册名称")
    String value() default ""; //as bean.name

    @XNote("标签，用于快速查找")
    String tag() default "";

    @XNote("特性，用于辅助配置")
    String[] attrs() default {};

    @XNote("同时注册类型，仅当名称非空时有效")
    boolean typed() default false;

    boolean remoting() default false; //是否开始远程服务
}
