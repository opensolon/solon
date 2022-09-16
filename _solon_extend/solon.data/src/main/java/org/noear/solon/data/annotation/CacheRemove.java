package org.noear.solon.data.annotation;

import org.noear.solon.annotation.*;
import java.lang.annotation.*;

/**
 * 缓存移除注解器
 *
 * 注意：针对 Controller、Service、Dao 等所有基于MethodWrap运行的目标，才有效
 *
 * @author noear
 * @since 1.0
 * */
@Inherited //要可继承
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRemove {
    /**
     * 缓存服务
     * */
    @Note("缓存服务")
    String service() default "";

    /**
     * 例：user_${user_id}
     * */
    @Note("缓存唯一标识，多个以逗号隔开")
    String keys() default "";

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @Note("清除缓存标签，多个以逗号隔开")
    String tags() default "";
}
