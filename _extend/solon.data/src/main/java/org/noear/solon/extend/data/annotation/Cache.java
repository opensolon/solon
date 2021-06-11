package org.noear.solon.extend.data.annotation;

import org.noear.solon.extend.data.around.CacheInterceptor;

import java.lang.annotation.*;

import org.noear.solon.annotation.*;

/**
 * 缓存注解器
 *
 * @author noear
 * @since 1.0
 * */
//@Around(value = CacheInterceptor.class, index = 111)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    /**
     * 缓存服务
     * */
    @Note("缓存服务")
    String service() default "";

    /**
     * 0表示采用cache service的默认是境
     * */
    @Note("缓存时间，0表示缓存服务的默认时间")
    int seconds() default 0;

    @Note("缓存唯一标识")
    String key() default "";

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @Note("缓存标签，多个以逗号隔开")
    String tags() default "";
}
