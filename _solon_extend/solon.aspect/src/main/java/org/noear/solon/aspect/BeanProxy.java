package org.noear.solon.aspect;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.InvocationHandler;

/**
 * @author noear
 * @since 1.6
 */
class BeanProxy implements BeanWrap.Proxy {
    static final BeanProxy global = new BeanProxy();

    InvocationHandler handler;

    private BeanProxy() {
    }

    protected BeanProxy(InvocationHandler handler) {
        this.handler = handler;
    }

    /**
     * 获取代理
     */
    @Override
    public Object getProxy(AopContext context, Object bean) {
        return new BeanInvocationHandler(context, bean, handler).getProxy();
    }
}
