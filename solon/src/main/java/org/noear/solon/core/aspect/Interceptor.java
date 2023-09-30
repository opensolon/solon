package org.noear.solon.core.aspect;


/**
 * 方法拦截器
 *
 * <pre><code>
 * @Target({ElementType.METHOD})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface Cache {
 *
 * }
 *
 * //或者直接注册
 * context.beanInterceptorAdd(Cache.class, 111, new CacheInterceptor());
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
