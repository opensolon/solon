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
    @XNote("been name")
    String value() default ""; //as bean.name

    @XNote("been tags")
    String tags() default "";

    boolean primary() default false;

    boolean remoting() default false; //是否开始远程服务
}
