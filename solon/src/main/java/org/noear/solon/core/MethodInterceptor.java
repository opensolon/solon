package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 方法拦截器（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface MethodInterceptor {
    /**
     * 调用
     *
     * @param obj 对象
     * @param method 方法
     * @param params 参数
     * @param args 参数值
     * @param invokeChain 调用链
     * */
    Object doInvoke(Object obj, Method method, Parameter[] params, Object[] args, MethodChain invokeChain) throws Throwable;
}
