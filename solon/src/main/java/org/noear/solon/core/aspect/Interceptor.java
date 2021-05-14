package org.noear.solon.core.aspect;


/**
 * 方法拦截器（通过 @Around 随载）
 *
 * <pre><code>
 * @Controller
 * public class DemoController{
 *     @Around(TranInterceptor.class)  //@Tran 注解即通过 @Around 实现
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
@FunctionalInterface
public interface Interceptor {
    /**
     * 拦截
     *
     * @param inv 调用者
     * */
    Object doIntercept(Invocation inv) throws Throwable;
}
