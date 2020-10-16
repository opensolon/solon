package org.noear.solon.annotation;

import org.noear.solon.extend.data.around.CacheRemoveInterceptor;

import java.lang.annotation.*;

/**
 * 缓存移除注解器
 *
 * @author noear
 * @since 1.0
 * */
@XAround(value = CacheRemoveInterceptor.class, index = -9)
@Inherited //要可继承
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XCacheRemove {
    /**
     * 缓存服务
     * */
    @XNote("缓存服务")
    String service() default "";
    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @XNote("清除缓存标签，多个以逗号隔开")
    String tags();
}
