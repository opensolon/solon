package org.noear.solon.annotation;

import org.noear.solon.extend.data.around.CachePutInvokeHandler;

import java.lang.annotation.*;

/**
 * 缓存更新注解器（之前有缓存才会被更新；不然无法进行类型检测）
 *
 * @author noear
 * @since 1.0
 * */
@XAround(value = CachePutInvokeHandler.class, index = -9)
@Inherited //要可继承
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XCacheUpdate {
    /**
     * 缓存服务
     * */
    @XNote("缓存服务")
    String service() default "";
    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @XNote("更新缓存标签，多个以逗号隔开")
    String tags();
}
