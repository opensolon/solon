package org.noear.solon.annotation;

import org.noear.solon.core.MethodHandler;

import java.lang.annotation.*;

/**
 * 触发器：围绕处理（争对 XController、XAction、XService、XDao 等所有基于MethodWrap运行的目标）
 *
 * <pre><code>
 * @XController
 * public class DemoController{
 *     @XAround(TranInvokeHandler.class)  //@XTran 注解即通过 @XAround 实现
 *     @XMapping("/demo/*")
 *     public String hello(){
 *         return "heollo world;";
 *     }
 * }
 *
 * //
 * // 注解传导示例：（用于简化使用）
 * //
 * @XAround(value = TranInvokeHandler.class, index = -7)
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface XTran {
 *     ....
 * }
 *
 * @XAround(value = CacheInvokeHandler.class, index = -8)
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface XCache {
 *     ...
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAround {
    /**
     * 调用处理程序
     * */
    Class<? extends MethodHandler> value();
    /**
     * 调用顺位
     * */
    int index() default 0;
}
