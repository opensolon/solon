package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodHolder;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * 方法拦截调用链（用于支持 @Around；有自己的顺排序号；执行按先进后面策略 ）
 *
 * @author noear
 * @since 1.0
 * */
public class InterceptorChainNode implements InterceptorChain{
    private final Interceptor interceptor;

    /**
     * 顺排序位（排完后，按先进后出策略执行）
     * */
    public final int index;
    public InterceptorChain next;

    public InterceptorChainNode(int i, Interceptor p) {
        index = i;
        interceptor = p;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        inv.next = next;
        return interceptor.doIntercept(inv);
    }
}
