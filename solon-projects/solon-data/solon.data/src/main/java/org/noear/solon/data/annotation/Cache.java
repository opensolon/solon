package org.noear.solon.data.annotation;

import java.lang.annotation.*;

import org.noear.solon.annotation.*;

/**
 * 缓存注解器
 *
 * 注意：针对 Controller、Service、Dao 等所有基于MethodWrap运行的目标，才有效
 *
 * @author noear
 * @since 1.0
 * */
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

    /**
     * 例：user_${user_id}
     * */
    @Note("缓存唯一标识，不能有逗号")
    String key() default "";

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @Note("缓存标签，多个以逗号隔开")
    String tags() default "";
}
