package org.noear.solon.data.annotation;

import org.noear.solon.annotation.*;
import java.lang.annotation.*;

/**
 * 缓存更新注解器（之前有缓存才会被更新；不然无法进行类型检测）
 *
 * @author noear
 * @since 1.0
 * */
//@Around(value = CachePutInterceptor.class, index = 110)
@Inherited //要可继承
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {
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
