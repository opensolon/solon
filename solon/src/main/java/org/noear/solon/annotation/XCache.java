package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 缓存器
 * */
@Inherited //要可继承
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XCache {
    /**
     * 缓存服务
     * */
    String caching() default "";

    /**
     * 当为0时，使用缓存服务本身的
     * */
    int seconds() default 0;

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    String tags() default "";

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    String clear() default "";
}
