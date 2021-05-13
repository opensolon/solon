package org.noear.solon.core.handle;


import org.noear.solon.core.wrap.MethodHolder;

/**
 * 方法拦截调用链（用于支持 @Around ）
 *
 * @author noear
 * @since 1.0
 * */
public interface InterceptorChain {
    /**
     * 拦截传递（target、args 是动态的，所以用参数传递；其它部分可以固化）
     *
     * @param inv 调用者
     */
    Object doIntercept(Invocation inv) throws Throwable;
}
