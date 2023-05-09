package org.noear.solon.annotation;

import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.*;


//:: /xxx/*
//:: /xxx/*.js
//:: /xxx/**
//:: /xxx/**/$*
//:: /xxx/{b_b}/{ccc}.js

/**
 * 路径映射
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
    @Alias("path")
    String value() default "";

    /**
     * 路径
     * */
    @Alias("value")
    String path() default "";

    /**
     * 请求方式
     * */
    MethodType[] method() default {MethodType.ALL};
    /**
     * 指定处理请求的提交内容类型（主要考虑与Swagger的描述兼容）
     * */
    String consumes() default "";
    /**
     * 指定返回的内容类型（主要考虑与Swagger的描述兼容）
     * */
    String produces() default "";

    /**
     * 是否申明为多分片（否则为自动识别）
     * */
    boolean multipart() default false;
}
