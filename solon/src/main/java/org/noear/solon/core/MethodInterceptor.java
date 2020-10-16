package org.noear.solon.core;


/**
 * 方法拦截器（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface MethodInterceptor {
    /**
     * 拦截
     *
     * @param obj 对象
     * @param method 方法
     * @param args 参数值
     * @param invokeChain 调用链
     * */
    Object doIntercept(Object obj, MethodHolder method, Object[] args, MethodChain invokeChain) throws Throwable;
}
