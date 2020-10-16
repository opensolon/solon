package org.noear.solon.core;


/**
 * 方法拦截器（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface XInterceptor {
    /**
     * 拦截
     *
     * @param target 目标对象
     * @param args 参数
     * @param chain 拦截链
     * */
    Object doIntercept(Object target, Object[] args, XInterceptorChain chain) throws Throwable;
}
