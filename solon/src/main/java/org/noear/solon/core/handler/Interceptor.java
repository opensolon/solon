package org.noear.solon.core.handler;


/**
 * 方法拦截器（通过 @XAround 随载）
 *
 * <pre><code>
 * @Controller
 * public class DemoController{
 *     @Around(TranInterceptor.class)  //@XTran 注解即通过 @XAround 实现
 *     @Mapping("/demo/*")
 *     public String hello(){
 *         return "heollo world;";
 *     }
 * }
 *
 * //
 * // 注解传导示例：（用于简化使用）
 * //
 * @Around(value = TranInterceptor.class, index = -7)
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface XTran {
 *     ....
 * }
 *
 * @Around(value = CacheInterceptor.class, index = -8)
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
public interface Interceptor {
    /**
     * 拦截
     *
     * @param target 目标对象
     * @param args 参数
     * @param chain 拦截链
     * */
    Object doIntercept(Object target, Object[] args, InterceptorChain chain) throws Throwable;
}
