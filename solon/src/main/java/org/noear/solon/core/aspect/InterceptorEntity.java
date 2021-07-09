package org.noear.solon.core.aspect;

/**
 * 拦截实体。存放拦截器和顺序位
 *
 * @author noear
 * @since 1.3
 */
public class InterceptorEntity implements Interceptor{
    /**
     * 顺排序位（排完后，按先进后出策略执行）
     */
    public final int index;
    private final Interceptor real;

    public InterceptorEntity(int index, Interceptor real) {
        this.index = index;
        this.real = real;
    }

    /**
     * 拦截
     * */
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        return real.doIntercept(inv);
    }
}
