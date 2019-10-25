package org.noear.solon.annotation;

import org.noear.solon.core.XMethod;

import java.lang.annotation.*;


//:: /xxx/*
//:: /xxx/*.js
//:: /xxx/**
//:: /xxx/**/$*
//:: /xxx/{b_b}/{ccc}.js

/**
 * 路径印射
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XMapping {
    String value() default "";
    XMethod[] method() default {XMethod.HTTP};
    String produces() default "";
    int index() default 0;//顺序位
}
