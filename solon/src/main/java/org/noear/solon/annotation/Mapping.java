package org.noear.solon.annotation;

import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;


//:: /xxx/*
//:: /xxx/*.js
//:: /xxx/**
//:: /xxx/**/$*
//:: /xxx/{b_b}/{ccc}.js

/**
 * 路径印射
 *
 * 一般附加在控制器和动作上
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {
    String value() default "";
    MethodType[] method() default {MethodType.HTTP};
    String consumes() default "";
    String produces() default "";

    /**
     * 顺序位（before 或 after =true 时有效）
     * */
    int index() default 0;
    boolean before() default false;
    boolean after() default false;
}
