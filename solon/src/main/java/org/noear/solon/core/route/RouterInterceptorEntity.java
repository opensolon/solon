package org.noear.solon.core.route;

/**
 * 路由拦截器节点
 *
 * @author noear
 * @since 1.12
 */
public class RouterInterceptorEntity {
    /**
     * 顺排序位（排完后，按先进后出策略执行）
     */
    public final int index;
    /**
     * 自己
     * */
    public final RouterInterceptor interceptor;

    public RouterInterceptorEntity(int index, RouterInterceptor interceptor) {
        this.index = index;
        this.interceptor = interceptor;
    }
}
