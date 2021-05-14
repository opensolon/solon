package org.noear.solon.core.aspect;

/**
 * @author noear
 * @since 1.3
 */
public class InterceptorEntity {
    /**
     * 顺排序位（排完后，按先进后出策略执行）
     */
    public final int index;
    public final Interceptor interceptor;

    public InterceptorEntity(int index, Interceptor interceptor) {
        this.index = index;
        this.interceptor = interceptor;
    }
}
