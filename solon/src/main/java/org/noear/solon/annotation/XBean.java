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

    @XNote("标签")
    String tags() default "";

    @XNote("同时注册类型")
    boolean typed() default false;

    boolean remoting() default false; //是否开始远程服务
}
